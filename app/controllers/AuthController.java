package controllers;

import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.jpa.Transactional;
import play.libs.Json;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import models.service.UserService;

public class AuthController extends Controller {
    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String REDIRECT_PATH = "/";
    
    public static User getUser() {
        return (User)Http.Context.current().args.get("user");
    }

    // returns an authToken
    @Transactional
    public Result login() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return badRequest(loginForm.errorsAsJson());
        }

        Login login = loginForm.get();

        User user = UserService.findByEmailAddressAndPassword(login.email, login.password);

        if (user == null) {
            return unauthorized();
        } else {
            String authToken = UserService.createToken(user);
            ObjectNode authTokenJson = Json.newObject();
            authTokenJson.put(AUTH_TOKEN, authToken);
            response().setCookie(AUTH_TOKEN, authToken);
            return ok(authTokenJson);
        }
    }
    
    @Transactional
    @Security.Authenticated(Secured.class)
    public Result logout() {
        response().discardCookie(AUTH_TOKEN);
        UserService.deleteAuthToken(getUser());
        return redirect(REDIRECT_PATH);
    }
    
    public static class Login {
        @Constraints.Required
        @Constraints.Email
        public String email;

        @Constraints.Required
        public String password;

    }
}
