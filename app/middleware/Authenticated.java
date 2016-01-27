package middleware;

import controllers.AuthController;
import models.User;
import models.service.UserService;
import play.libs.Json;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Authenticated extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        String[] authTokenHeaderValues = ctx.request().headers().get(AuthController.AUTH_TOKEN_HEADER);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1)
                && (authTokenHeaderValues[0] != null)) {
            User user = UserService.checkJWT(authTokenHeaderValues[0]);
            if (user != null) {
                return Json.stringify(Json.toJson(user));
            }
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return unauthorized();
    }
}
