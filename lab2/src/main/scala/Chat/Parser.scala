package Chat

import scala.annotation.tailrec

class UnexpectedTokenException(msg: String) extends Exception(msg) {}

class Parser(tokensIt: Iterator[(String, Token)]):
  import ExprTree.*
  import Chat.Token.*

  // Start the process by reading the first token.
  var curTuple: (String, Token) = tokensIt.next()

  def curValue: String = curTuple._1
  def curToken: Token = curTuple._2

  /** Reads the next token and assigns it into the global variable curTuple */
  def readToken(): Unit = curTuple = tokensIt.next()

  /** "Eats" the expected token and returns it value, or terminates with an
    * error.
    */
  private def eat(token: Token): String =
    if token == curToken then
      val tmp = curValue
      readToken()
      tmp
    else expected(token)

  /** Complains that what was found was not expected. The method accepts
    * arbitrarily many arguments of type Token
    */
  private def expected(token: Token, more: Token*): Nothing =
    expected(more.prepended(token))
  private def expected(tokens: Seq[Token]): Nothing =
    val expectedTokens = tokens.mkString(" or ")
    throw new UnexpectedTokenException(
      s"Expected: $expectedTokens, found: $curTuple",
    )

  /** the root method of the parser: parses an entry phrase */
  def parsePhrases(): ExprTree = {
    if curToken == BONJOUR then {
      readToken()
      if curToken == EOL then return Greeting
    }

    curToken match {
      case QUEL | COMBIEN => parsePriceRequest()
      case JE             => parseUserRequest()
      case _              => expected(BONJOUR, QUEL, COMBIEN, JE)
    }
  }

  /** Parses a price request phrase starting with QUEL or COMBIEN
   * and returns the corresponding PriceRequest statement.
   */
  private def parsePriceRequest(): Statement = {
    if curToken == QUEL then
      // Using readToken instead of eat(QUEL) as
      // we already checked if it was QUEL
      readToken()
      eat(ETRE)
      eat(LE)
      eat(PRIX)
      eat(DE)
    else if curToken == COMBIEN then {
      readToken()
      eat(COUTER)
    } else expected(QUEL, COMBIEN)

    PriceRequest(parseLogicExpression())
  }

  /** Parses a user request phrase starting with JE VOULOIR,
   * dispatching to either a BalanceRequest or an OrderRequest.
   */
  private def parseUserRequest(): Statement =
    eat(JE)
    eat(VOULOIR)

    if curToken == CONNAITRE then
      readToken()
      eat(MON)
      eat(SOLDE)

      BalanceRequest
    else if curToken == COMMANDER then {
      readToken()

      OrderRequest(parseLogicExpression())
    } else expected(CONNAITRE, COMMANDER)

  /** Parses a single order expression composed of a number followed by a product.
   *
   * @return an OrderExpression wrapping the parsed number and product
   */
  private def parseOrderExpression(): Expression =
    OrderExpression(parseNumber(), parseProduct())

  /** Parses a numeric token and wraps it in a NumberExpression.
   *
   * @return a NumberExpression holding the parsed integer value
   */
  private def parseNumber(): NumberExpression =
    val amount = eat(NUM).toInt
    NumberExpression(amount)

  /** Parses a product token and an optional brand token,                                                                                                                            
   * returning a ProductExpression with an optional brand name.
   */
  private def parseProduct(): ProductExpression =
    val productName = eat(PRODUIT)
    ProductExpression(
      productName,
      if curToken == MARQUE then Some(eat(MARQUE)) else None,
    )

  /** Parses a left-associative chain of order expressions joined by ET or OU,
   * producing a nested tree of AndExpression or OrExpression nodes.
   *
   * @return the root expression of the parsed chain
   */
  private def parseLogicExpression(): Expression =
    // Must be recursive with always the left hand-signed
    @tailrec
    def parseLogicTail(left: Expression): Expression =
      curToken match {
        case ET =>
          readToken()
          parseLogicTail(AndExpression(left, parseOrderExpression()))
        case OU =>
          readToken()
          parseLogicTail(OrExpression(left, parseOrderExpression()))
        case _ => left
      }

    parseLogicTail(parseOrderExpression())
