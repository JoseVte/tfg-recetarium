package middleware;

import controllers.AuthController;
import models.User;
import models.service.UserService;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Admin extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(AuthController.AUTH_TOKEN_HEADER);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1)
                && (authTokenHeaderValues[0] != null)) {
            user = UserService.checkJWT(authTokenHeaderValues[0]);
            System.out.println(user);
            if (user != null && user.isAdmin()) {
                ctx.args.put("user", user);
                ctx.response().discardCookie(AuthController.AUTH_TOKEN);
                ctx.response().setCookie(AuthController.AUTH_TOKEN, authTokenHeaderValues[0]);
                return user.email;
            }
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return unauthorized();
    }
}
