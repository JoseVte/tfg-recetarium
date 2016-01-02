package middleware;

import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;

public class Anonymous extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx) {
        if (ctx.request().headers().get("X-AUTH-TOKEN") == null || ctx.request().headers().get("X-AUTH-TOKEN")[0] == null) {
            return "anonymous";
        }

        return null;
    }

    @Override
    public Result onUnauthorized(Context ctx) {
        return unauthorized();
    }
}
