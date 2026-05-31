import Data.{ProductImpl, ProductService}
import munit.ScalaCheckSuite

class ProductServiceTest extends ScalaCheckSuite {
  val productService: ProductService = new ProductImpl

  test("getPrice returns correct price for boxer beer"):
    assert(productService.getPrice("biere", "boxer") == 1.0)

  test("getPrice returns correct price for tenebreuse beer"):
    assert(productService.getPrice("biere", "tenebreuse") == 4.0)

  test("getPrice returns correct price for maison croissant"):
    assert(productService.getPrice("croissant", "maison") == 2.0)

  test("getPrice throws on unknown product"):
    intercept(productService.getPrice("pizza", "maison"))

  test("getPrice throws on unknown brand for known product"):
    intercept(productService.getPrice("biere", "guiness"))

  test("default brand for product biere is boxer"):
    assert(productService.getDefaultBrand("biere") == "boxer")

  test("default brand for product croissant is maison"):
    assert(productService.getDefaultBrand("croissant") == "maison")

  test("default throws on an unknown product"):
    intercept(productService.getDefaultBrand("pizza"))

  test("products contains biere and croissant"):
    assert(productService.products.contains("biere"))
    assert(productService.products.contains("croissant"))
}
