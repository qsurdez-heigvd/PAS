package Data
import ProductService.*

object ProductService:
  type BrandName = String
  type ProductName = String

trait ProductService:
  /** Get the price of a given product and brand
    *
    * @param product
    *   the name of the product
    * @param brand
    *   the name of the brand
    * @return
    *   the price in CHF
    */
  def getPrice(product: ProductName, brand: BrandName): Double

  /** Get the default brand for a given product
    *
    * @param product
    *   the given product
    * @return
    *   the default brand
    */
  def getDefaultBrand(product: ProductName): BrandName

  /** For all available products list all their available brands.
    *
    * @return
    *   a map of all available products and all their brands.
    */
  def products: Map[ProductName, Iterable[BrandName]]

class ProductImpl extends ProductService:
  import ProductService.*

  // TODO ask if we can return Option instead of throwing exception ?
  private val allProducts: Map[ProductName, Seq[(BrandName, Double)]] =
    Map(
      "biere" -> Seq(
        ("boxer", 1.0),
        ("farmer", 1.0),
        ("wittekop", 2.0),
        ("punkipa", 3.0),
        ("jackhammer", 3.0),
        ("tenebreuse", 4.0),
      ),
      "croissant" -> Seq(
        ("maison", 2.0),
        ("cailler", 2.0),
      ),
    )

  // If the tests were not relying on the method's signature
  // I would have change it to return an Option[Double]
  override def getPrice(
      product: ProductName,
      brand: BrandName,
  ): Double =
    allProducts
      .getOrElse(
        product,
        throw new Exception(s"No such product: ${product}"),
      )
      .find(_._1 == brand)
      .map(_._2)
      .getOrElse(
        throw new Exception(s"No such brand: ${brand} for product: ${product}"),
      )

  // If the tests were not relying on the method's signature
  // I would have change it to return an Option[BrandName]
  override def getDefaultBrand(product: ProductName): BrandName =
    allProducts
      .getOrElse(
        product,
        throw new Exception(s"No such product: ${product}"),
      )
      .head
      ._1

  override def products: Map[ProductName, Iterable[BrandName]] =
    allProducts.view.mapValues(_.map(_._1)).toMap
end ProductImpl
