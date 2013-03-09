package bootstrap.liftweb

import net.liftweb.http._
import _root_.net.liftweb.http.provider._
import org.codeandmagic.ukgist.UKGistRest

/**
 * A class that's instantiated early and run.  It allows the application
 * to modify lift's environment
 */
class Boot {
  def boot {
    //LiftRules.addToPackages("org.codeandmagic.ukgist")

    /* Things that completely bypass LiftFilter */
    /*LiftRules.liftRequest.prepend({
      case Req("download" :: _, _, _) => false
      case Req("images" :: _, _, _) => false
      case Req("favicon" :: Nil, "ico", _) => false
    })*/

    /* Registers the main dispatch table for REST calls (as stateless API) */
    LiftRules.statelessDispatch.append(UKGistRest)

    /* Defines encoding as UTF-8 */
    LiftRules.early.append((req: HTTPRequest) => req.setCharacterEncoding("utf-8"))

    /* Use HTML5 for rendering */
    LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))

    /* Transforms exceptions to proper JSON responses */
    //LiftRules.exceptionHandler.prepend({ case (mode, req, ex) => ConvertableToResponse.toJsonResponse(ex) })

    /* Headers that allow jQuery requests to work properly even if Origin header is null */
    /*LiftRules.supplimentalHeaders = s => s.addHeaders(
      List(HTTPParam("X-Lift-Version", LiftRules.liftVersion),
        HTTPParam("Access-Control-Allow-Origin", "*"),
        HTTPParam("Access-Control-Allow-Credentials", "true"),
        HTTPParam("Access-Control-Allow-Methods", "GET, POST, PUT, OPTIONS"),
        HTTPParam("Access-Control-Allow-Headers", "Authorization,Keep-Alive,User-Agent,Cache-Control,Content-Type")
      ))
    */
  }
}