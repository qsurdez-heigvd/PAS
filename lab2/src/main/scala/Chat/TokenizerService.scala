package Chat

import Chat.Token.*
import Data.ProductService

class TokenizerService(
    dictionary: Map[String, String],
    productSvc: ProductService,
):

  // The assumption that the products are static is made
  private val productNames = productSvc.products.keySet
  private val brandNames = productSvc.products.values.flatten.toSet

  private val keywords: Map[String, Token] = Map(
    "bonjour"  -> Token.BONJOUR,
    "vouloir"  -> Token.VOULOIR,
    "je"       -> Token.JE,
    "commander"-> Token.COMMANDER,
    "connaitre"-> Token.CONNAITRE,
    "mon"      -> Token.MON,
    "solde"    -> Token.SOLDE,
    "combien"  -> Token.COMBIEN,
    "couter"   -> Token.COUTER,
    "quel"     -> Token.QUEL,
    "etre"     -> Token.ETRE,
    "le"       -> Token.LE,
    "prix"     -> Token.PRIX,
    "de"       -> Token.DE,
    "et"       -> Token.ET,
    "ou"       -> Token.OU,
  )

  /** Separate the user's input into tokens
    * @param input
    *   the user's input
    * @return
    *   an iterator over the tokens of the input. The last token is always EOL.
    */
  def tokenize(input: String): Iterator[(String, Token)] =
    val cleaned = input
      .replaceAll("[.,!?*'\\s]", " ")
      .trim()

    // Filtering empty strings with " +"
    val tokens =
      cleaned.split(" +").map(_.toLowerCase).map(tokenizeWord)
    (tokens :+ ("EOL", Token.EOL)).iterator

  private def tokenizeWord(word: String): (String, Token) =
    val normalized = dictionary.getOrElse(word, word)

    val token = keywords.get(normalized)
      .orElse(if productNames.contains(normalized) then Some(Token.PRODUIT) else None)
      .orElse(if brandNames.contains(normalized) then Some(Token.MARQUE) else None)
      .orElse(if normalized.forall(Character.isDigit) then Some(Token.NUM) else None)
      .getOrElse(Token.UNKNOWN)
    (normalized, token)
end TokenizerService
