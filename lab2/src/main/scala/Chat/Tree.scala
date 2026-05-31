package Chat

import Data.ProductService.{BrandName, ProductName}

/** This sealed trait represents a node of the tree.
  */
sealed trait ExprTree

/** Declarations of the nodes' types.
  */
object ExprTree:

  /** A statement is a top-level ExprTree that triggers an action or produces a
    * response (e.g. a greeting, a price inquiry, an order). Statements do not
    * necessarily evaluate to a value themselves, they are the entry points
    * processed by the analyzer to generate a reply.
    */
  sealed trait Statement extends ExprTree

  case object Greeting extends Statement

  /** Price request statement giving back the total price of the evaluated
    * expression
    * @param products
    *   the expression to be evaluated
    */
  case class PriceRequest(products: Expression) extends Statement

  /** Order request statement evaluating the price to reduce from the user's
    * account
    * @param products
    *   the expressin to be evaluated
    */
  case class OrderRequest(products: Expression) extends Statement

  /** Balance request statement giving back the balance of the user's account
    */
  case object BalanceRequest extends Statement

  /** An expression is a composable ExprTree that evaluates to a numeric price.
    * Expressions form the operand tree of a statement: they can represent a
    * quantity, a product, a line item (order), or a combination of items joined
    * by AND / OR logic.
    */
  sealed trait Expression extends ExprTree

  /** Basic numerical expression from our grammar
    * @param value
    *   value assigned to it
    */
  case class NumberExpression(value: Int) extends Expression

  /** Basic product expression from our grammar
    * @param product
    *   the product name assigned to it
    * @param brand
    *   the possible brand assigned to it
    */
  case class ProductExpression(product: ProductName, brand: Option[BrandName])
      extends Expression

  /** Expression that combines two sub-expressions additively (logical AND):
    * both sides are always evaluated and their prices summed. It's
    * left-associative.
    *
    * @param left
    *   the left-hand sub-expression
    * @param right
    *   the right-hand sub-expression
    */
  case class AndExpression(left: Expression, right: Expression)
      extends Expression

  /** Expression that selects the cheaper of two sub-expressions (logical OR):
    * only the lower-cost branch is ordered or priced. It's left-associated.
    *
    * @param left
    *   the left-hand sub-expression
    * @param right
    *   the right-hand sub-expression
    */
  case class OrExpression(left: Expression, right: Expression)
      extends Expression

  /** Expression pairing a quantity with a product, representing a single line
    * item.
    *
    * @param amount
    *   the number of units to order
    * @param product
    *   the product (and optional brand) to order
    */
  case class OrderExpression(
      amount: NumberExpression,
      product: ProductExpression,
  ) extends Expression
