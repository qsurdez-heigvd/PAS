package Chat
import Data.{AccountService, ProductService, Session}

class AnalyzerService(productSvc: ProductService, accountSvc: AccountService):
  import ExprTree.*

  // TODO replace the calls to evaluateExpression and cheaperBranch with the
  // eval.func
  private val eval = ExpressionEvaluator(productSvc)

  /** Return the output text of the current node, in order to write it in
    * console.
    * @return
    *   the output text of the current node
    */
  def reply(session: Session)(t: ExprTree): String =
    t match
      // Example case
      case Greeting =>
        session.getCurrentUser match
          case Some(username) => s"Hello ${username} !"
          case None           => "Hello !"

      case PriceRequest(products) =>
        s"Cela coûte CHF ${evaluateExpression(products)}."

      case OrderRequest(products) =>
        session.getCurrentUser match {
          case None => "Veuillez d'abord vous identifier."
          case Some(user) =>
            val total = evaluateExpression(products)
            accountSvc.purchase(user, total) match {
              case None =>
                s"Solde insuffisant. Votre solde actuel est de CHF ${accountSvc.getAccountBalance(user)}."
              case Some(newBalance) =>
                s"Voici donc ${describeExpression(products)} ! Cela coûte CHF $total et votre nouveau solde est de CHF $newBalance."
            }
        }

      case BalanceRequest =>
        session.getCurrentUser match {
          case None => "Veuillez d'abord vous identifier."
          case Some(user) =>
            s"Le montant actuel de votre solde est de CHF ${accountSvc.getAccountBalance(user)}."
        }

      case _: ExprTree =>
        throw new IllegalArgumentException(
          s"Unexpected expression at top level: $t",
        )

  /** Recursively computes the total price of a product expression.
   *
   * @param expr the expression to evaluate
   * @return the total price in CHF
   */
  private def evaluateExpression(expr: Expression): Double =
    eval.evaluateExpression(expr)

  /** Builds a readable description of a product expression (e.g. "2 Farmer").
   *
   * @param expr the expression to describe
   * @return a string describing the ordered items
   */
  private def describeExpression(expr: Expression): String = expr match {
    case OrderExpression(
          NumberExpression(n),
          ProductExpression(product, brand),
        ) =>
      val b = brand.getOrElse(productSvc.getDefaultBrand(product))
      s"$n $b"
    case AndExpression(left, right) =>
      s"${describeExpression(left)} et ${describeExpression(right)}"
    case OrExpression(left, right) =>
      describeExpression(cheaperBranch(left, right))
    case _ => ""
  }

  /** Returns the cheaper of two expressions by comparing their evaluated prices.
   *
   * @param left  the left-hand branch of a And or Or expression
   * @param right the right-hand branch of a And or Or expression
   * @return the expression with the lower or equal price
   */
  private def cheaperBranch(left: Expression, right: Expression): Expression =
    eval.cheaperBranch(left, right)
end AnalyzerService
