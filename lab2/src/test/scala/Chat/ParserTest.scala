package Chat

import munit.ScalaCheckSuite
import Chat.Token.*
import Chat.ExprTree.*

class ParserTest extends ScalaCheckSuite {

  def makeParser(tokens: (String, Token)*): Parser =
    Parser(tokens.iterator)

  // Greeting

  test("bonjour followed by EOL returns Greeting"):
    val parser = makeParser(("bonjour", BONJOUR), ("", EOL))
    assert(parser.parsePhrases() == Greeting)

  test("bonjour followed by a non-EOL token falls through to user request"):
    val parser = makeParser(
      ("bonjour", BONJOUR),
      ("je", JE),
      ("vouloir", VOULOIR),
      ("connaitre", CONNAITRE),
      ("mon", MON),
      ("solde", SOLDE),
      ("", EOL),
    )
    assert(parser.parsePhrases() == BalanceRequest)

  // Balance request

  test("je veux connaitre mon solde returns BalanceRequest"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("connaitre", CONNAITRE),
      ("mon", MON),
      ("solde", SOLDE),
      ("", EOL),
    )
    assert(parser.parsePhrases() == BalanceRequest)

  // Price request

  test("quel est le prix de ... returns PriceRequest"):
    val parser = makeParser(
      ("quel", QUEL),
      ("est", ETRE),
      ("le", LE),
      ("prix", PRIX),
      ("de", DE),
      ("2", NUM),
      ("biere", PRODUIT),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        PriceRequest(
          OrderExpression(NumberExpression(2), ProductExpression("biere", None)),
        ),
    )

  test("combien coute ... returns PriceRequest"):
    val parser = makeParser(
      ("combien", COMBIEN),
      ("coute", COUTER),
      ("1", NUM),
      ("croissant", PRODUIT),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        PriceRequest(
          OrderExpression(
            NumberExpression(1),
            ProductExpression("croissant", None),
          ),
        ),
    )

  // Order request

  test("je veux commander returns OrderRequest"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("commander", COMMANDER),
      ("3", NUM),
      ("biere", PRODUIT),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        OrderRequest(
          OrderExpression(NumberExpression(3), ProductExpression("biere", None)),
        ),
    )

  // Brands

  test("product with brand is parsed correctly"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("commander", COMMANDER),
      ("1", NUM),
      ("biere", PRODUIT),
      ("punkipa", MARQUE),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        OrderRequest(
          OrderExpression(
            NumberExpression(1),
            ProductExpression("biere", Some("punkipa")),
          ),
        ),
    )

  test("product without brand has None brand"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("commander", COMMANDER),
      ("1", NUM),
      ("biere", PRODUIT),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        OrderRequest(
          OrderExpression(NumberExpression(1), ProductExpression("biere", None)),
        ),
    )

  // Logic operators

  test("two products with ET returns AndExpression"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("commander", COMMANDER),
      ("2", NUM),
      ("biere", PRODUIT),
      ("et", ET),
      ("1", NUM),
      ("croissant", PRODUIT),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        OrderRequest(
          AndExpression(
            OrderExpression(
              NumberExpression(2),
              ProductExpression("biere", None),
            ),
            OrderExpression(
              NumberExpression(1),
              ProductExpression("croissant", None),
            ),
          ),
        ),
    )

  test("two products with OU returns OrExpression"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("commander", COMMANDER),
      ("2", NUM),
      ("biere", PRODUIT),
      ("ou", OU),
      ("1", NUM),
      ("croissant", PRODUIT),
      ("", EOL),
    )
    assert(
      parser.parsePhrases() ==
        OrderRequest(
          OrExpression(
            OrderExpression(
              NumberExpression(2),
              ProductExpression("biere", None),
            ),
            OrderExpression(
              NumberExpression(1),
              ProductExpression("croissant", None),
            ),
          ),
        ),
    )

  test("three products with ET are left-associative"):
    val parser = makeParser(
      ("quel", QUEL),
      ("est", ETRE),
      ("le", LE),
      ("prix", PRIX),
      ("de", DE),
      ("1", NUM),
      ("biere", PRODUIT),
      ("et", ET),
      ("2", NUM),
      ("biere", PRODUIT),
      ("et", ET),
      ("3", NUM),
      ("croissant", PRODUIT),
      ("", EOL),
    )
    val oe1 =
      OrderExpression(NumberExpression(1), ProductExpression("biere", None))
    val oe2 =
      OrderExpression(NumberExpression(2), ProductExpression("biere", None))
    val oe3 =
      OrderExpression(NumberExpression(3), ProductExpression("croissant", None))
    assert(
      parser.parsePhrases() == PriceRequest(
        AndExpression(AndExpression(oe1, oe2), oe3),
      ),
    )

  test("ET and OU can be mixed, left-associative"):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("commander", COMMANDER),
      ("1", NUM),
      ("biere", PRODUIT),
      ("et", ET),
      ("2", NUM),
      ("croissant", PRODUIT),
      ("ou", OU),
      ("3", NUM),
      ("biere", PRODUIT),
      ("punkipa", MARQUE),
      ("", EOL),
    )
    val oe1 =
      OrderExpression(NumberExpression(1), ProductExpression("biere", None))
    val oe2 =
      OrderExpression(NumberExpression(2), ProductExpression("croissant", None))
    val oe3 = OrderExpression(
      NumberExpression(3),
      ProductExpression("biere", Some("punkipa")),
    )
    assert(
      parser.parsePhrases() == OrderRequest(
        OrExpression(AndExpression(oe1, oe2), oe3),
      ),
    )

  // Error handling

  test("unknown token as first token throws UnexpectedTokenException"):
    val parser = makeParser(("xyz", UNKNOWN), ("", EOL))
    intercept[UnexpectedTokenException]:
      parser.parsePhrases()

  test("QUEL without ETRE throws UnexpectedTokenException"):
    val parser = makeParser(("quel", QUEL), ("", EOL), ("", EOL))
    intercept[UnexpectedTokenException]:
      parser.parsePhrases()

  test(
    "JE VOULOIR with unexpected action token throws UnexpectedTokenException",
  ):
    val parser = makeParser(
      ("je", JE),
      ("vouloir", VOULOIR),
      ("", UNKNOWN),
      ("", EOL),
    )
    intercept[UnexpectedTokenException]:
      parser.parsePhrases()

  test("COMBIEN without COUTER throws UnexpectedTokenException"):
    val parser = makeParser(("combien", COMBIEN), ("", UNKNOWN), ("", EOL))
    intercept[UnexpectedTokenException]:
      parser.parsePhrases()

}
