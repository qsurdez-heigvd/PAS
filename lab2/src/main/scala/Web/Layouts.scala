package Web

import Data.MessageService.Username
import Data.Session
import Web.StaticRoutes
import scalatags.Text
import scalatags.Text.all.*
import scalatags.Text.tags2

/** Assembles the method used to layout ScalaTags Organized into children
  * objects by concern: [[Nav]], [[Board]], [[Forms]], [[Pages]].
  */
object Layouts:
  type NavbarItem = (String, String)
  val botName: String = "Bot-tender"

  object Nav:
    val messageBoardItem: NavbarItem = ("Go to the message board", "/")
    val loginItem: NavbarItem = ("Login", "/login")
    val logoutItem: NavbarItem = ("Logout", "/logout")

    /** Buils a navbar for the session state: greeting + logout link for
      * authenticated users, or a login link for unauthenticated users
      *
      * @param session
      *   the current session
      * @return
      *   a `nav` tag
      */
    def apply(session: Session): Tag =
      session.getCurrentUser
        .map(u => apply((s"Hello $u!", ""), logoutItem))
        .getOrElse(apply(loginItem))

    /** Builds a navbar from an explicit list of items.
      *
      * @param first
      *   the mandatory first item (label, href) pair
      * @param rest
      *   additional items
      * @return
      *   a `nav` tag containing the given items
      */
    def apply(first: NavbarItem, rest: NavbarItem*): Tag =
      build(first +: rest)

    /** Renders a sequence of (label, href) pairs into a `<nav>` element. Items
      * with an empty href are rendered as plain `<span>`, otherwise as `<a>`
      *
      * @param items
      *   ordered sequence of (display text, link) pairs
      * @return
      *   a `<nav>` tag
      */
    def build(items: Seq[NavbarItem]): Tag =
      tags2.nav(
        a(cls := "nav-brand", botName),
        items.map { (msg, link) =>
          div(
            cls := "nav-item",
            if link.isEmpty then span(msg) else a(msg, href := link),
          )
        },
      )

  end Nav

  object Board:
    val loading: Tag = p(
      "Please wait, the messages are loading !",
      style := "text-align: center;",
    )
    val empty: Tag =
      p("No messages have been sent yet", style := "text-align: center;")

    /** Wraps content in the `#boardMessage` div, which the JS WebSocket client
      * targets when pushing message updates.
      * @param content
      *   zero or more modifiers to embed inside the container
      * @return
      *   a `<div id="boardMessage">` tag
      */
    def container(content: Modifier*): Tag =
      div(id := "boardMessage", content)

    /** Renders a single chat message row
      *
      * @param text
      *   the message body
      * @param author
      *   the sender's username, shown as a style author span
      * @param mention
      *   optional recipient. If present, rendered as a highlighted `@mention`
      *   prefix
      * @return
      *   a `<div class="msg">` fragment
      */
    def entry(
        text: String,
        author: Username,
        mention: Option[Username] = None,
    ): Frag =
      div(
        cls := "msg",
        span(author, cls := "author"),
        span(
          cls := "msg-content",
          mention.map(m => span(s"@$m ", cls := "mention")),
          text,
        ),
      )
  end Board

  object Forms:

    /** Wraps content under a `<h2>` heading inside a `<div>`.
      *
      * @param title
      *   the section title
      * @param content
      *   the form elements or other modifiers to include
      * @return
      *   a `<div>` with the heading and content
      */
    def section(title: String, content: Modifier*): Tag =
      div(h2(title), content)

    /** Builds the message input form. Submission is handled by
      * `submitMessageForm()` in the given JS, with the input bound to the
      * `message` field.
      *
      * @param errorContent
      *   optional error message modifier displayed above the input
      * @return
      *   a `<form>` tag wired for AJAX message submission
      */
    def sendMessage(errorContent: Modifier = frag()): Tag =
      inputForm(
        labelText = "Your message:",
        placeHolderText = "Write your message",
        inputMods = Seq(name := "message"),
        errorContent = errorContent,
        formMods = Seq(onsubmit := "submitMessageForm(); return false"),
      )

    /** Builds a username input form for login or registration. The action
      * (login vs. register) is set by the caller.
      *
      * @param errorContent
      *   optional error message modifier displayed above the input
      * @return
      *   a `<form>` tag with a username text field
      */
    def username(errorContent: Modifier = frag()): Tag =
      inputForm(
        labelText = "Your username",
        placeHolderText = "Write your username",
        inputMods = Seq(name := "username"),
        errorContent = errorContent,
        formMods = Seq(method := "post"),
      )

    private def inputForm(
        labelText: String,
        placeHolderText: String,
        inputMods: Seq[Modifier] = Nil,
        errorContent: Modifier = frag(),
        formMods: Seq[Modifier] = Nil,
    ): Tag =
      form(
        formMods,
        div(id := "errorDiv", cls := "errorMsg", errorContent),
        label(`for` := "messageInput", labelText),
        input(
          id := "messageInput",
          `type` := "text",
          placeholder := placeHolderText,
          required,
          inputMods,
        ),
        input(`type` := "submit", value := "Send"),
      )
  end Forms

  object Pages:
    private val cssStyleSheets = Seq(
      link(
        rel := "stylesheet",
        href := s"${StaticRoutes.CSS_URL_PREFIX}/main.css",
      ),
    )

    private val jsScripts = Seq(
      script(src := s"${StaticRoutes.JS_URL_PREFIX}/main.js"),
    )

    /** Assembles a full HTML document with the app's standard head (charset,
      * viewport, CSS) and body (navbar, content area, scripts).
      *
      * @param pageHeader
      *   the `<nav>` tag rendered at the top of the body
      * @param scripts
      *   optional `<script>` tags appended at the end of the body
      * @param pageContent
      *   the main body content
      * @return
      *   a full `doctype("html")` document
      */
    def page(pageHeader: Tag, scripts: Seq[Tag] = Nil)(
        pageContent: Frag*,
    ): doctype =
      doctype("html"):
        html(
          head(
            meta(charset := "utf-8"),
            meta(
              name := "viewport",
              content := "width=device-width, initial-scale=1",
            ),
            tags2.title(botName),
            cssStyleSheets,
          ),
          body(
            pageHeader,
            div(cls := "content", pageContent),
            scripts,
          ),
        )

    /** Renders the main chatroom page which consists of: the message board
      * container and the send form
      *
      * @param session
      *   the current session, used to build the navbar
      * @return
      *   the home page document
      */
    def home(session: Session): doctype =
      page(Nav(session), jsScripts)(
        Board.container(Board.loading),
        Forms.sendMessage(),
      )

    /** Renders the login/register page with two forms side by side.
      *
      * @param loginError
      *   error login message shown above the login form. Empty by default.
      * @param registerError
      *   error register message shown above the register form. Empty by
      *   default.
      * @return
      *   the login/register page document
      */
    def login(
        loginError: String = "",
        registerError: String = "",
    ): doctype =
      page(Nav(Nav.messageBoardItem))(
        Forms.section(
          "Login",
          Forms.username(span(loginError))(action := "/login"),
        ),
        Forms.section(
          "Register",
          Forms.username(span(registerError))(action := "/register"),
        ),
      )

    /** Renders a generic success confirmation page with a custom status
      * message.
      * @param headerItems
      *   the navbar action item to show (login or logout link)
      * @param msg
      *   the success message displayed in the body
      * @return
      *   the success page document
      */
    def success(headerItems: NavbarItem)(msg: String): doctype =
      page(Nav(Nav.messageBoardItem, headerItems))(
        p(msg),
      )

  end Pages

end Layouts
