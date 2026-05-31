package Web

import Chat.{
  AnalyzerService,
  Parser,
  TokenizerService,
  UnexpectedTokenException,
}
import Data.MessageService.Username
import Data.{AccountService, MessageService, Session, SessionService}
import Web.Layouts.Board
import cask.util.Ws
import upickle.default.*

import scala.collection.mutable
import castor.Context.Simple.global

/** Assembles the routes dealing with the message board:
  *   - One route to display the home page
  *   - One route to send the new messages as JSON
  *   - One route to subscribe with websocket to new messages
  *
  * @param log
  */
class MessagesRoutes(
    tokenizerSvc: TokenizerService,
    analyzerSvc: AnalyzerService,
    msgSvc: MessageService,
    accountSvc: AccountService,
    sessionSvc: SessionService,
)(implicit val log: cask.Logger)
    extends cask.Routes:
  import Decorators.getSession
  import MessagesRoutes.*

  private var channels: mutable.Set[castor.Actor[Ws.Event]] = mutable.Set.empty

  @getSession(
    sessionSvc,
  ) // This decorator fills the `(session: Session)` part of the `index` method.
  @cask.get("/")
  def index()(session: Session) =
    Layouts.Pages.home(session)

  /** Processes an incoming chat message in one of the three modes:
    *   - `@bot <text>`, parses and evaluates a bot command, stores both the
    *     user message an the bot reply, and broadcasts both to all subscribers
    *   - `@<username> <text>`, stores the message with a mention tag
    *   - plain text, stores the message
    *
    * Rejects empty messages or unauthenticate requests before any write.
    * Notifies all active WebSocket subscribers on every successful write.
    *
    * @param msg
    *   the raw message string from the JSON request body
    * @param session
    *   the current session, injected by [[getSession]]
    * @return
    *   [[MessageResponse]] with success, or an error description
    */
  @getSession(
    sessionSvc,
  )
  @cask.postJson("/send")
  def sendMessages(msg: String)(session: Session) = {
    val trimmed = msg.trim
    if trimmed.isEmpty then MessageResponse(false, "Please provide a message.")
    else
      session.getCurrentUser match {
        case None =>
          MessageResponse(false, "You must be logged in to send a message.")
        case Some(user) =>
          trimmed match {
            case s if s.startsWith(botPrefix) =>
              handleBotMessage(session, user, s.drop(botPrefix.length).trim)
            case MessagesRoutes.mentionPattern(mention, content) =>
              msgSvc.add(
                user,
                Board.entry(content, user, Some(mention)),
                Some(mention),
              )
              notifyAllChannels()
              success
            case _ =>
              msgSvc.add(user, Board.entry(trimmed, user))
              notifyAllChannels()
              success
          }
      }
  }

  /** Opens a WebSocket connection for real-time updates.
    *
    * On connection, immediately pushes the current message history on the new
    * subscriber. Registers the channel so future [[notifyAllChannels]] calls
    * reach it as well. Unregisters the channel on close.
    *
    * @return
    *   a [[cask.WebsocketResult]] managing the channel lifecycle
    */
  @cask.websocket("/subscribe")
  def subscribe(): cask.WebsocketResult =
    cask.WsHandler { chan =>
      notifyChannel(chan)
      channels.add(chan)
      cask.WsActor { case Ws.Close(_, _) =>
        channels.remove(chan)
      }
    }

  /** Deletes all store messages and notifies all WebSocket subscribers, then
    * redirects the client to the home page.
    */
  @cask.get("/clearHistory")
  def clearHistory(): Unit = {
    msgSvc.deleteHistory()
    notifyAllChannels()
    cask.Redirect("/")
  }

  /** Sends the latest 20 messages (rendered as HTML) to a single WebSocket
    * channel, or the empty board placeholder if there are no messages.
    *
    * @param chan
    *   the target WebSocket
    */
  private def notifyChannel(chan: castor.Actor[Ws.Event]): Unit =
    msgSvc.getLatestMessages(20).map(_._2.render) match {
      case Nil      => chan.send(Ws.Text(Board.empty.render))
      case messages => chan.send(Ws.Text(messages.mkString("\n")))
    }

  /** Pushes the current message boar state to every active WebSocket subscriber
    */
  private def notifyAllChannels(): Unit =
    channels.foreach(notifyChannel)

  /** Parses and evaluates a bot command, storing both the user's message and
    * the bot's reply as linked messages (via `replyToId`)
    *
    * @param session
    *   the current session, forwarded to the analyser for context
    * @param user
    *   the authenticated sender
    * @param content
    *   the bot command text, already stripped of the `@bot` prefix
    * @return
    *   [[MessageResponse]] with the bot's reply on success, or a parse error
    *   description
    */
  private def handleBotMessage(
      session: Session,
      user: Username,
      content: String,
  ): MessageResponse =
    if content.isEmpty then
      MessageResponse(false, "Please provide a message for the bot.")
    else
      try
        val tokens = tokenizerSvc.tokenize(content).toList
        val stmt = Parser(tokens.iterator).parsePhrases()
        val msgId = msgSvc.add(
          user,
          Board.entry(content, user, Some(botName)),
          Some(botName),
          Some(stmt),
        )
        val reply = analyzerSvc.reply(session)(stmt)
        msgSvc.add(
          botName,
          Board.entry(reply, botName),
          replyToId = Some(msgId),
        )
        notifyAllChannels()
        success
      catch
        case e: UnexpectedTokenException => MessageResponse(false, e.getMessage)

  initialize()

end MessagesRoutes

object MessagesRoutes:
  case class MessageResponse(success: Boolean, err: String) derives ReadWriter
  private val success = MessageResponse(true, "")
  private val botName = Layouts.botName
  private val botPrefix = "@bot"
  private val mentionPattern = """^@(\w+)\s+(.+)$""".r
end MessagesRoutes
