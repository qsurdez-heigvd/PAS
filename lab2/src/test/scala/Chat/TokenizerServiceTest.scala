import Chat.{Token, TokenizerService}
import Data.ProductImpl
import munit.ScalaCheckSuite
import Utils.Dictionary

class TokenizerServiceTest extends ScalaCheckSuite {
  val tokenizerService: TokenizerService =
    new TokenizerService(Dictionary.dictionary, new ProductImpl)

  def tokens(input: String): List[(String, Token)] =
    tokenizerService.tokenize(input).toList

  def tokenTypes(input: String): List[Token] =
    tokens(input).map(_._2)

  test("tokenizes bonjour"):
    assert(tokenTypes("bonjour") == List(Token.BONJOUR, Token.EOL))

  test("normalizes j'aimerais to JE + VOULOIR"):
    val result = tokens("j'aimerais")
    assert(result.head == ("je", Token.JE))
    assert(result(1) == ("vouloir", Token.VOULOIR))

  test("tokenizes a number"):
    assert(tokens("12").head == ("12", Token.NUM))

  test("tokenizes product name bière"):
    assertEquals(tokens("bière").head, ("biere", Token.PRODUIT))

  test("tokenizes brand name punkipa"):
    assertEquals(tokens("punkipa").head, ("punkipa", Token.MARQUE))

  test("unknown word becomes UNKNOWN token"):
    assertEquals(tokens("xyz").head._2, Token.UNKNOWN)

  test("always ends with EOL"):
    assert(tokenTypes("bonjour je veux").last == Token.EOL)

  test("full command sentence tokenizes correctly"):
    val result = tokenTypes("je veux commander 2 bières et 1 croissant")
    assert(
      result ==
        List(
          Token.JE,
          Token.VOULOIR,
          Token.COMMANDER,
          Token.NUM,
          Token.PRODUIT,
          Token.ET,
          Token.NUM,
          Token.PRODUIT,
          Token.EOL,
        ),
    )

}
