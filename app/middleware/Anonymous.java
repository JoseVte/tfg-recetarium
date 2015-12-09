package middleware;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Anonymous extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        if (ctx.request().headers().isEmpty()) {
        	return "anonymous";
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return unauthorized();
    }
}
