package Chat

import Chat.ExprTree.{
  AndExpression,
  Expression,
  NumberExpression,
  OrExpression,
  OrderExpression,
  ProductExpression,
}
import Data.ProductService

class ExpressionEvaluator(productSvc: ProductService):
  /** Recursively computes the total price of a product expression.
    *
    * @param expr
    *   the expression to evaluate
    * @return
    *   the total price in CHF
    */
  def evaluateExpression(expr: Expression): Double = expr match {
    case OrderExpression(
          NumberExpression(n),
          ProductExpression(product, brand),
        ) =>
      val b = brand.getOrElse(productSvc.getDefaultBrand(product))
      n * productSvc.getPrice(product, b)
    case AndExpression(left, right) =>
      evaluateExpression(left) + evaluateExpression(right)
    case OrExpression(left, right) =>
      evaluateExpression(cheaperBranch(left, right))
    // There's no production rule in the grammar that would generate a bare NumberExpression
    // or a bare ProductExpression, thus they are not evaluated by themselves
    case e =>
      throw new IllegalArgumentException(
        s"Unexpected expression in evaluateExpression: $e",
      )
  }

  /** Returns the cheaper of two expressions by comparing their evaluated
    * prices.
    *
    * @param left
    *   the left-hand branch of a And or Or expression
    * @param right
    *   the right-hand branch of a And or Or expression
    * @return
    *   the expression with the lower or equal price
    */
  def cheaperBranch(left: Expression, right: Expression): Expression =
    if evaluateExpression(left) <= evaluateExpression(right) then left
    else right
