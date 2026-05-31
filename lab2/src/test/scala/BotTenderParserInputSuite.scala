import munit.ScalaCheckSuite
import org.scalacheck.Prop.*

import java.io.ByteArrayOutputStream
import Utils.Dictionary
import Chat.TokenizerService
import Data.*
import Chat.AnalyzerService

class BotTenderParserInputSuite extends ScalaCheckSuite {
  val productSvc: ProductService = new ProductImpl()
  val tokenizerSvc: TokenizerService = new TokenizerService(
    Dictionary.dictionary,
    productSvc,
  )
  val accountSvc: AccountService = new AccountImpl()
  val sessionSvc: SessionService = new SessionImpl()
  val analyzerSvc: AnalyzerService = new AnalyzerService(productSvc, accountSvc)

  val session = sessionSvc.create()

  val evaluateInput =
    MainParser.evaluateInput(tokenizerSvc, analyzerSvc, session)

  /** Capture output of an evaluated input as string.
    *
    * @param input
    *   the input to evalute
    * @return
    *   the string that would have been printed to the terminal
    */
  def evaluateAndCapture(input: String): String = {
    // capture output for testing therefore it is not shown in the terminal
    val outCapture = new ByteArrayOutputStream
    Console.withOut(outCapture) {
      evaluateInput(input)
    }
    outCapture.toString().stripLineEnd
  }

  // You can use this test to debug any input
  test("inputting") {
    evaluateInput("quitter")
  }

  test("inputting 'quitter'") {
    assertEquals(evaluateAndCapture("quitter"), "Adieu.")
  }

  test("inputting 'quitter' should leave the loop") {
    assertEquals(evaluateInput("quitter"), false)
  }

  test("inputting anything except 'quitter' should not leave the loop") {
    assertEquals(evaluateInput("bonjour"), true)
    assertEquals(evaluateInput("anything"), true)
  }

  test("changing user") {
    assertEquals(evaluateAndCapture("bonjour"), "Hello !")
    // Set the new current user to John (automatically creating the account if needed)
    accountSvc.setCurrent("John", session)
    assertEquals(evaluateAndCapture("bonjour"), "Hello John !")
    // Unset the current user (no one is authenticated anymore)
    session.reset()
    assertEquals(evaluateAndCapture("bonjour"), "Hello !")
  }
}
