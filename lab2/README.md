# Bot-Tender – Labo Future

**Auteur :** Quentin Surdez

## Choix architecturaux et d'implémentation

### Vue d'ensemble

Ce laboratoire étend Bot-Tender avec la gestion asynchrone des commandes via `scala.concurrent.Future`. Les fichiers ajoutés ou modifiés sont :

| Fichier | Statut | Rôle |
|---|---|---|
| `Data/ClaudeAccountImpl` | nouveau | Solde thread-safe via `TrieMap` + CAS |
| `Chat/ClaudeOrderPreparationService` | nouveau | Préparation asynchrone des articles |
| `Chat/ClaudeBotResponseStrategy` | nouveau | Trait stratégie + deux implémentations |
| `Web/MessagesRoutes` | modifié | Reçoit une `BotResponseStrategy` injectée |
| `MainFuture` | modifié | Câble les implémentations async |
| `MainChatroom` | modifié | Câble l'implémentation sync |

---

### Stratégie `BotResponseStrategy` — injection dans la couche web

La stratégie est injectée dans `MessagesRoutes`, et non dans `AnalyzerService`. Cela laisse le domaine métier (`AnalyzerService`) complètement agnostique de l'asynchronisme. C'est la couche web qui porte la décision de dispatch.

Le type de retour `Either` distingue les deux cas :

```
Left(reply)             → synchrone : poster reply immédiatement
Right((ack, eventual))  → async     : poster ack maintenant,
                                      poster le résultat de eventual quand le Future complète
```

`MessagesRoutes.handleBotMessage` se réduit à un pattern match sur cet `Either` :

```scala
strategy.respond(session, user, stmt) match
  case Left(reply) =>
    msgSvc.add(botName, reply) ; notifyAllChannels()
  case Right((ack, completion)) =>
    msgSvc.add(botName, ack) ; notifyAllChannels()
    completion.foreach { msg =>
      msgSvc.add(botName, msg, mention = Some(user)) ; notifyAllChannels()
    }
```

`MessagesRoutes` est ainsi identique pour les deux modes d'exécution, sans duplication.

Les deux implémentations du trait :
- **`ClaudeSyncBotResponseStrategy(analyzerSvc)`** — délègue à `AnalyzerService.reply`, retourne toujours `Left`.
- **`ClaudeAsyncBotResponseStrategy(analyzerSvc, prepSvc)`** — retourne `Right(...)` pour un `OrderRequest` (préparation async via `prepSvc`), et `Left(analyzerSvc.reply(...))` pour les autres cas.

---

### `ClaudeOrderPreparationService` — préparation asynchrone

#### Parallélisme et séquentialité

L'expression est d'abord aplatie en `Seq[UnitItem]` (un élément = une unité physique à préparer ; le `OR` est résolu en choisissant la branche la moins chère avant extraction). Les unités sont ensuite regroupées par marque :

```
groupBy(brand) ──→ brand A : foldLeft + flatMap  ← séquentiel dans le groupe
               ──→ brand B : foldLeft + flatMap  ← séquentiel dans le groupe
               ──→ ...
Future.sequence(tous les groupes)                ← parallèle entre groupes
```

La séquentialité au sein d'une marque est obtenue par `foldLeft` avec `flatMap` : le Future du prochain article n'est créé que dans le callback du précédent, ce qui impose l'ordre. Le parallélisme entre marques vient du fait que tous les Futures de groupes sont lancés avant l'appel à `Future.sequence`.

#### Gestion des échecs partiels

Chaque `FutureOps.randomSchedule(...)` est suivi d'un `.recover { case _ => None }`. Les articles réussis sont `Some(UnitItem)`, les échoués sont `None`. Seuls les `Some` figurent dans la commande servie. Le débit du compte (`ClaudeAccountImpl.purchase`) n'a lieu qu'une fois tous les articles prêts, et seulement pour le montant des articles effectivement servis.

Chaque marque dispose de ses propres paramètres `(mean, std, successRate)`. Les bières simples (boxer, cailler) sont rapides et fiables ; les bières artisanales (ténébreuse, punkipa) prennent plus de temps et échouent plus souvent.

---

### `ClaudeAccountImpl` — accès concurrent au solde

`AccountImpl` s'appuie sur un `mutable.Map` non thread-safe. Plusieurs Futures pouvant débiter un compte simultanément, on utilise un `TrieMap` avec une opération **CAS** (*Compare-And-Swap*) :

```
purchase(user, amount):
  1. lire la balance courante
  2. calculer newBalance = balance - amount
  3. TrieMap.replace(user, balance, newBalance)  ← atomique
  4. si le CAS échoue (un autre thread a modifié la valeur entre-temps) → recommencer (@tailrec)
```

Un seul thread peut réussir le CAS à chaque tour ; la progression est donc garantie sans `synchronized`.

---

### Ce qui n'a pas été modifié

`AnalyzerService`, `AccountImpl`, `MessageImpl`, `Parser` et la logique du labo 3 sont conservés intacts.
