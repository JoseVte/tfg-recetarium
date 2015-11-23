package controllers;

import models.User;
import models.service.UserService;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        User user = null;
        String[] authTokenHeaderValues = ctx.request().headers().get(AuthController.AUTH_TOKEN_HEADER);
        if ((authTokenHeaderValues != null) && (authTokenHeaderValues.length == 1) && (authTokenHeaderValues[0] != null)) {
            user = UserService.findByAuthToken(authTokenHeaderValues[0]);
            if (user != null) {
                ctx.args.put("user", user);
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