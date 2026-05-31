import Data.{
  AccountImpl,
  AccountService,
  ProductImpl,
  ProductService,
  Session,
  SessionImpl,
  SessionService,
}
import munit.ScalaCheckSuite
import Chat.AnalyzerService
import Chat.ExprTree.{
  AndExpression,
  BalanceRequest,
  Greeting,
  NumberExpression,
  OrExpression,
  OrderExpression,
  OrderRequest,
  PriceRequest,
  ProductExpression,
}

class AnalyserServiceTest extends ScalaCheckSuite {

  val productSvc: ProductService = new ProductImpl()
  val accountSvc: AccountService = new AccountImpl()
  val analyzerSvc: AnalyzerService = new AnalyzerService(productSvc, accountSvc)
  val sessionSvc: SessionService = new SessionImpl()

  def freshSession(): Session = sessionSvc.create()

  def freshAuthSession(user: String): Session =
    val session = sessionSvc.create()
    accountSvc.setCurrent(user, session)
    session

  // Greeting

  test("greeting without user returns Hello"):
    val session = freshSession()
    assert(analyzerSvc.reply(session)(Greeting) == "Hello !")

  test("greeting with authenticated user includes username"):
    val session = freshAuthSession("Samantha")
    assert(analyzerSvc.reply(session)(Greeting) == "Hello Samantha !")

  // BalanceRequest

  test("balance request without auth returns identification message"):
    val session = freshSession()
    assert(
      analyzerSvc.reply(session)(BalanceRequest) ==
        "Veuillez d'abord vous identifier.",
    )

  test("balance request with auth returns correct balance"):
    val session = freshAuthSession("Michelle")
    val reply = analyzerSvc.reply(session)(BalanceRequest)
    assert(reply.contains("30.0"))

  // PriceRequest

  test("price request for 1 boxer return CHF 1.0"):
    val session = freshSession()
    val expr = PriceRequest(
      OrderExpression(
        NumberExpression(1),
        ProductExpression("biere", Some("boxer")),
      ),
    )
    assert(analyzerSvc.reply(session)(expr) == "Cela coûte CHF 1.0.")

  test("price request for 2 punkipas returns CHF 6.0."):
    val session = freshSession()
    val expr = PriceRequest(
      OrderExpression(
        NumberExpression(2),
        ProductExpression("biere", Some("punkipa")),
      ),
    )
    assert(analyzerSvc.reply(session)(expr) == "Cela coûte CHF 6.0.")

  test("price request for 1 croissant (default brand) returns CHF 2.0"):
    val session = freshSession()
    val expr = PriceRequest(
      OrderExpression(
        NumberExpression(1),
        ProductExpression("croissant", None),
      ),
    )
    assert(analyzerSvc.reply(session)(expr) == "Cela coûte CHF 2.0.")

  test("price request with AndExpression sums both prices"):
    val session = freshSession()
    // 1 boxer (1.0) + 1 croissant (2.0) = 3.0
    val expr = PriceRequest(
      AndExpression(
        OrderExpression(
          NumberExpression(1),
          ProductExpression("biere", Some("boxer")),
        ),
        OrderExpression(
          NumberExpression(1),
          ProductExpression("croissant", None),
        ),
      ),
    )
    assert(analyzerSvc.reply(session)(expr) == "Cela coûte CHF 3.0.")

  test("price request with OrExpression picks the cheaper branch"):
    val session = freshSession()
    // 1 tenebreuse (4.0) ou 1 boxer (1.0) => 1.0
    val expr = PriceRequest(
      OrExpression(
        OrderExpression(
          NumberExpression(1),
          ProductExpression("biere", Some("tenebreuse")),
        ),
        OrderExpression(
          NumberExpression(1),
          ProductExpression("biere", Some("boxer")),
        ),
      ),
    )
    assert(analyzerSvc.reply(session)(expr) == "Cela coûte CHF 1.0.")

  // OrderRequest

  test("order request without auth returns identification message"):
    val session = freshSession()
    val expr = OrderRequest(
      OrderExpression(NumberExpression(1), ProductExpression("biere", None)),
    )
    assert(
      analyzerSvc.reply(session)(expr) ==
        "Veuillez d'abord vous identifier.",
    )

  test("order request deducts price from balance"):
    val session = freshAuthSession("charlie")
    val expr = OrderRequest(
      OrderExpression(
        NumberExpression(1),
        ProductExpression("biere", Some("punkipa")),
      ),
    )
    val reply = analyzerSvc.reply(session)(expr)
    // punkipa costs 3.0; balance starts at 30.0 => new balance 27.0
    assert(reply.contains("27.0"))
    assert(reply.contains("3.0"))

  test("order request reply includes item description"):
    val session = freshAuthSession("diana")
    val expr = OrderRequest(
      OrderExpression(
        NumberExpression(2),
        ProductExpression("biere", Some("farmer")),
      ),
    )
    val reply = analyzerSvc.reply(session)(expr)
    assert(reply.contains("2 farmer"))

  test("order request with insufficient balance returns error message"):
    val svc = new AccountImpl()
    val analyzer = new AnalyzerService(productSvc, svc)
    val session = sessionSvc.create()
    svc.addAccount("poor", 1.0)
    session.setCurrentUser("poor")
    val expr = OrderRequest(
      OrderExpression(
        NumberExpression(1),
        ProductExpression("biere", Some("tenebreuse")),
      ),
    )
    val reply = analyzer.reply(session)(expr)
    assert(reply.toLowerCase.contains("insuffisant"))

  test("order request with OrExpression orders the cheaper option"):
    val session = freshAuthSession("eve")
    // 1 tenebreuse (4.0) ou 1 boxer (1.0) => orders boxer
    val expr = OrderRequest(
      OrExpression(
        OrderExpression(
          NumberExpression(1),
          ProductExpression("biere", Some("tenebreuse")),
        ),
        OrderExpression(
          NumberExpression(1),
          ProductExpression("biere", Some("boxer")),
        ),
      ),
    )
    val reply = analyzerSvc.reply(session)(expr)
    assert(reply.contains("boxer"))
    assert(reply.contains("1.0"))

  test("order request with AndExpression orders all items"):
    val session = freshAuthSession("frank")
    // 1 boxer (1.0) et 1 croissant maison (2.0) => 3.0
    val expr = OrderRequest(
      AndExpression(
        OrderExpression(
          NumberExpression(1),
          ProductExpression("biere", Some("boxer")),
        ),
        OrderExpression(
          NumberExpression(1),
          ProductExpression("croissant", None),
        ),
      ),
    )
    val reply = analyzerSvc.reply(session)(expr)
    assert(reply.contains("boxer"))
    assert(reply.contains("maison"))
    assert(reply.contains("3.0"))

}
