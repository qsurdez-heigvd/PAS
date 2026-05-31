package Chat

import Data.ProductService
import Data.AccountService
import Data.ProductService.{BrandName, ProductName}

import scala.concurrent.Future
import scala.concurrent.duration.{Duration, DurationInt}

class OrderPreparationService(
    productSvc: ProductService,
    accountSvc: AccountService,
):
  import ExprTree.*

  private val eval = ExpressionEvaluator(productSvc)

  private case class UnitItem(
      product: ProductName,
      brand: BrandName,
      price: Double,
  )

  /** Param table per mark with (mean, std, successRate) for using
    * [FutureOps.randomSchedule]
    */
  private val brandParams: Map[BrandName, (Duration, Duration, Double)] = Map(
    // Beer brands
    "boxer" -> (2.seconds, 500.millis, 0.90),
    "farmer" -> (3.seconds, 500.millis, 0.80),
    "wittekop" -> (3.seconds, 1.second, 0.75),
    "punkipa" -> (4.seconds, 500.millis, 0.70),
    "jackhammer" -> (4.seconds, 1.second, 0.65),
    "tenebreuse" -> (5.seconds, 500.millis, 0.60),
    // Croissant brands
    "maison" -> (2.seconds, 1.second, 0.90),
    "cailler" -> (3.seconds, 1.second, 0.70),
  )

  private def extractUnits(expr: Expression): Seq[UnitItem] = expr match
    case OrderExpression(NumberExpression(n), ProductExpression(product, brand)) =>
      val b = brand.getOrElse(productSvc.getDefaultBrand(product))
      val price = productSvc.getPrice(product, b)
      Seq.fill(n)(UnitItem(product, b, price))
    case AndExpression(left, right) =>
      extractUnits(left) ++ extractUnits(right)
    case OrExpression(left, right) =>
      extractUnits(eval.cheaperBranch(left, right))

  private def prepareBrandSequentially(units: Seq[UnitItem]): Future[Seq[Option[UnitItem]]] =
    units.foldeLeft(Future.successful(Seq.empty[Option[UnitItem]])) {}  
    

