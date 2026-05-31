package Web

/** Assembles the routes dealing with static files.
  */
class StaticRoutes()(implicit val log: cask.Logger) extends cask.Routes:

  @cask.staticResources("/resources/css")
  def staticStyleSheets() = "css"

  @cask.staticResources("/resources/js")
  def staticJavaScripts() = "js"

  initialize()
end StaticRoutes

object StaticRoutes:
  /** These prefixes are used by the app if there's a need to refer to these
    * resources. We couldn't use them in the [[staticResources]] annotation as
    * it requires a literal to be passed at compile-time. But having it in a
    * companion object makes sense as the only place to change if a change needs
    * to be made will be in this file only.
    */
  val CSS_URL_PREFIX = "/resources/css"
  val JS_URL_PREFIX = "/resources/js"

end StaticRoutes
