# Guide d'implémentation – Labo Future

## Architecture cible

```
Chat/
  ClaudeOrderPreparationService.scala   ← logique async (nouvelle)
  ClaudeBotResponseStrategy.scala       ← trait stratégie + 2 impls (nouvelle)
Data/
  ClaudeAccountImpl.scala               ← TrieMap thread-safe (nouvelle)
Web/
  MessagesRoutes.scala                  ← accepte BotResponseStrategy (modifiée)
MainChatroom.scala                      ← câble stratégie sync (modifié)
MainFuture.scala                        ← câble stratégie async (modifié)
```

---

## Étape 1 — `Data/ClaudeAccountImpl.scala`

**Pourquoi :** `AccountImpl` utilise `mutable.Map`, non thread-safe. Plusieurs Futures peuvent
débiter le même compte en parallèle.

**Classe :** `ClaudeAccountImpl extends AccountService`

**Champ :**
```scala
private val accounts: TrieMap[String, Double] = TrieMap.empty
// import scala.collection.concurrent.TrieMap
```

**Méthodes `getAccountBalance`, `addAccount`, `isAccountExisting`, `setCurrent` :**
Identiques à `AccountImpl`, juste substituer `mutable.Map` par `TrieMap`.
(`putIfAbsent` remplace le `if !exists then addOne`)

**Méthode `purchase` — CAS avec retry :**
```scala
override def purchase(user: String, amount: Double): Option[Double] =
  @tailrec
  def attempt(): Option[Double] =
    accounts.get(user) match
      case Some(balance) if balance >= amount =>
        val newBalance = balance - amount
        if accounts.replace(user, balance, newBalance) then Some(newBalance)
        else attempt()   // un autre thread a modifié entre-temps → réessayer
      case _ => None
  attempt()
```
`TrieMap.replace(k, old, new)` est atomique : retourne `true` seulement si la valeur
actuelle est toujours `old`. Le `@tailrec` garantit la terminaison.

---

## Étape 2 — `Chat/ClaudeOrderPreparationService.scala`

**Pourquoi :** contient toute la logique async (préparation, retry partiel, débit final)
séparément du reste pour ne pas polluer `AnalyzerService`.

**Classe :** `ClaudeOrderPreparationService(productSvc: ProductService, accountSvc: AccountService)`

### 2a — Données internes

```scala
private case class UnitItem(product: ProductName, brand: BrandName, price: Double)
```

Table des paramètres par marque `(mean, std, successRate)` :
```scala
private val brandParams: Map[BrandName, (Duration, Duration, Double)] = Map(
  "boxer"      -> (2.seconds, 500.millis, 0.90),
  "farmer"     -> (3.seconds, 1.second,   0.80),
  "wittekop"   -> (2.seconds, 500.millis, 0.85),
  "punkipa"    -> (4.seconds, 1.second,   0.75),
  "jackhammer" -> (3.seconds, 1.second,   0.80),
  "tenebreuse" -> (5.seconds, 1.second,   0.70),
  "maison"     -> (3.seconds, 1.second,   0.85),
  "cailler"    -> (2.seconds, 500.millis, 0.90),
)
```

### 2b — `extractUnits(expr: Expression): Seq[UnitItem]`

Parcours récursif de l'arbre. Le `OR` se résout en choisissant la branche la moins chère
**avant** d'extraire les unités (même logique que `cheaperBranch` dans `AnalyzerService`) :

```
OrderExpression(NumberExpression(n), ProductExpression(product, brandOpt))
  → Seq.fill(n)(UnitItem(product, brand, price))   // n unités identiques

AndExpression(l, r)  → extractUnits(l) ++ extractUnits(r)
OrExpression(l, r)   → extractUnits(cheaperBranch(l, r))
```

*(copier `evaluateExpression` et `cheaperBranch` depuis `AnalyzerService` en privé)*

### 2c — `prepareBrandSequentially(units: Seq[UnitItem]): Future[Seq[Option[UnitItem]]]`

Articles d'une même marque préparés **l'un après l'autre** :
```scala
units.foldLeft(Future.successful(Seq.empty[Option[UnitItem]])) { (acc, unit) =>
  acc.flatMap { results =>
    val (mean, std, rate) = brandParams.getOrElse(unit.brand, (2.seconds, 500.millis, 1.0))
    FutureOps.randomSchedule(mean, std, rate)
      .map(_ => results :+ Some(unit))
      .recover { case _ => results :+ None }   // échec → None, pas de propagation
  }
}
```
Le `flatMap` garantit que le Future suivant n'est **créé** qu'une fois le précédent terminé.

### 2d — `prepare(user: String, products: Expression): Future[String]`

```scala
def prepare(user: String, products: Expression): Future[String] =
  val units   = extractUnits(products)
  val grouped = units.groupBy(_.brand)
  Future.sequence(grouped.values.map(prepareBrandSequentially)).map { brandResults =>
    val allOptions  = brandResults.flatten.toSeq
    val served      = allOptions.flatten           // retire les None
    val hasFailures = allOptions.exists(_.isEmpty)
    if served.isEmpty then
      "Votre commande a échoué."
    else
      val total = served.map(_.price).sum
      accountSvc.purchase(user, total) match
        case None =>
          s"Solde insuffisant. Votre solde actuel est de CHF ${accountSvc.getAccountBalance(user)}."
        case Some(newBalance) =>
          val desc = describeServed(served)
          val note = if hasFailures then " (commande partielle)" else ""
          s"Voici donc $desc$note ! Cela coûte CHF $total et votre nouveau solde est de CHF $newBalance."
  }
```

`Future.sequence` lance **tous les groupes de marques en parallèle** (les Futures sont créés
avant l'appel, ils démarrent immédiatement). Ce Future global ne peut pas échouer car chaque
item a un `.recover`.

**`describeServed` :**
```scala
private def describeServed(units: Seq[UnitItem]): String =
  units.groupBy(u => (u.product, u.brand))
    .map { case ((_, brand), items) => s"${items.size} $brand" }
    .mkString(" et ")
```

---

## Étape 3 — `Chat/ClaudeBotResponseStrategy.scala`

**Pourquoi :** permet à `MessagesRoutes` d'être identique pour le mode sync et async.
La stratégie est injectée dans la couche web, pas dans le domaine.

### 3a — Trait

```scala
trait BotResponseStrategy:
  def respond(
      session: Session,
      user: String,
      stmt: ExprTree,
  ): Either[String, (String, Future[String])]
  //  Left(reply)            → réponse synchrone
  //  Right((ack, eventual)) → poster ack maintenant, eventual plus tard
```

### 3b — `ClaudeSyncBotResponseStrategy(analyzerSvc: AnalyzerService)`

```scala
class ClaudeSyncBotResponseStrategy(analyzerSvc: AnalyzerService)
    extends BotResponseStrategy:
  override def respond(session: Session, user: String, stmt: ExprTree) =
    Left(analyzerSvc.reply(session)(stmt))
```

### 3c — `ClaudeAsyncBotResponseStrategy(analyzerSvc: AnalyzerService, prepSvc: ClaudeOrderPreparationService)`

```scala
class ClaudeAsyncBotResponseStrategy(
    analyzerSvc: AnalyzerService,
    prepSvc: ClaudeOrderPreparationService,
) extends BotResponseStrategy:
  import ExprTree.OrderRequest
  override def respond(session: Session, user: String, stmt: ExprTree) =
    stmt match
      case OrderRequest(products) =>
        Right("Votre commande est en cours de préparation.", prepSvc.prepare(user, products))
      case _ =>
        Left(analyzerSvc.reply(session)(stmt))
```

---

## Étape 4 — Modifier `Web/MessagesRoutes.scala`

### 4a — Changer le constructeur

Remplacer `analyzerSvc: AnalyzerService` et `accountSvc: AccountService` par :
```scala
strategy: BotResponseStrategy,
```

Mettre à jour l'import :
```scala
import Chat.{ BotResponseStrategy, Parser, TokenizerService, UnexpectedTokenException }
import Data.{ MessageService, Session, SessionService }   // retirer AccountService
```

### 4b — Remplacer `handleBotMessage`

Remplacer le corps (depuis `val tokens = ...` jusqu'à la fin du `try`) par :

```scala
val tokens = tokenizerSvc.tokenize(content).toList
val stmt   = Parser(tokens.iterator).parsePhrases()
val msgId  = msgSvc.add(user, Board.entry(content, user, Some(botName)), Some(botName), Some(stmt))
strategy.respond(session, user, stmt) match
  case Left(reply) =>
    msgSvc.add(botName, Board.entry(reply, botName), replyToId = Some(msgId))
    notifyAllChannels()
  case Right((ack, completion)) =>
    msgSvc.add(botName, Board.entry(ack, botName), replyToId = Some(msgId))
    notifyAllChannels()
    completion.foreach { finalMsg =>
      msgSvc.add(botName, Board.entry(finalMsg, botName, Some(user)), Some(user), replyToId = Some(msgId))
      notifyAllChannels()
    }
success
```

`foreach` est utilisé (pas `onComplete` ni `Await`) car `prepare` ne peut pas échouer
(tous les items ont un `.recover`).

---

## Étape 5 — Modifier `MainChatroom.scala`

Après `val analyzerSvc`, ajouter :
```scala
val strategy = new ClaudeSyncBotResponseStrategy(analyzerSvc)
```

Changer l'instanciation de `MessagesRoutes` :
```scala
MessagesRoutes(tokenizerSvc, strategy, msgSvc, sessionSvc)
```

---

## Étape 6 — Modifier `MainFuture.scala`

```scala
val accountSvc: AccountService = new ClaudeAccountImpl()   // thread-safe
val analyzerSvc  = new AnalyzerService(productSvc, accountSvc)
val prepSvc      = new ClaudeOrderPreparationService(productSvc, accountSvc)
val strategy     = new ClaudeAsyncBotResponseStrategy(analyzerSvc, prepSvc)
// msgSvc reste MessageConcurrentImpl (déjà fourni)

// dans allRoutes :
MessagesRoutes(tokenizerSvc, strategy, msgSvc, sessionSvc)
```

---

## Checklist finale

- [ ] `ClaudeAccountImpl` compile et `purchase` est thread-safe (TrieMap + CAS)
- [ ] `ClaudeOrderPreparationService.prepare` retourne un `Future` qui ne fail jamais
- [ ] Même marque → `foldLeft` + `flatMap` (séquentiel)
- [ ] Marques différentes → `Future.sequence` (parallèle)
- [ ] Débit du compte uniquement sur les articles servis, uniquement à la fin
- [ ] `MessagesRoutes` n'importe plus `AnalyzerService` ni `AccountService`
- [ ] `MainChatroom` fonctionne toujours (stratégie sync)
- [ ] `MainFuture` fonctionne avec la stratégie async
- [ ] `sbt compile` passe sans erreur
