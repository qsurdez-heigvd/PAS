package Web

import Data.{AccountService, Session, SessionService}
import Web.Decorators.getSession
import Web.Layouts.Pages
import scalatags.Text.all.span
import scalatags.Text.implicits.stringFrag

/** Assembles the routes dealing with the users:
  *   - One route to display the login form and register form page
  *   - One route to process the login form and display the login success page
  *   - One route to process the register form and display the register success
  *     page
  *   - One route to logout and display the logout success page
  *
  * The username of the current session user is stored inside a cookie called
  * `username`.
  */
class UsersRoutes(accountSvc: AccountService, sessionSvc: SessionService)(
    implicit val log: cask.Logger,
) extends cask.Routes:

  /** Displays the login registration page */
  @cask.get("/login")
  def index() =
    Layouts.Pages.login()

  /** Processes a login form submission. Sets the session user if the account
    * exists, otherwises rerenders the login page with an error message.
    *
    * @param username
    *   the submitted username.
    * @param session
    *   the current session, injected by [[getSession]]
    * @return
    *   the success page on valid login, or the login page with an error.
    */
  @getSession(sessionSvc)
  @cask.postForm("/login")
  def loginForm(username: String)(session: Session) =
    if accountSvc.isAccountExisting(username)
    then
      accountSvc.setCurrent(username, session)
      Layouts.Pages.success(Layouts.Nav.logoutItem)(
        s"You are now logged in as $username",
      )
    else
      Layouts.Pages
        .login(loginError = "The user does not exist, please register first.")

  /** Processes a registration form submission. Creates an account and sets the
    * session user if the username is not already taken, otherwise rerenders the
    * plogin page with an error message.
    * @param username
    *   the username of the new user
    * @param session
    *   the current session, injected by [[getSession]]
    * @return
    *   the success page on valid registration, or the login page with an error
    */
  @getSession(sessionSvc)
  @cask.postForm("/register")
  def registerForm(username: String)(session: Session) =
    if !accountSvc.isAccountExisting(username)
    then
      accountSvc.setCurrent(username, session)
      Layouts.Pages.success(Layouts.Nav.logoutItem)(
        s"You have registered as $username and are now logged in.",
      )
    else
      Layouts.Pages
        .login(registerError = "This user already exist, please login.")

  /** Clears the current session and displays a logout confirmation page.
    *
    * @param session
    *   the current session, injected by [[getSession]]
    * @return
    *   the success page confirming logout
    */
  @getSession(sessionSvc)
  @cask.route("/logout", methods = Seq("post", "get"))
  def logout()(session: Session) = {
    session.reset()
    Layouts.Pages.success(Layouts.Nav.loginItem)("You are now logged out!")
  }

  initialize()

end UsersRoutes
