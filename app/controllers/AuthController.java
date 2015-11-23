package controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.TypeUser;
import models.User;
import models.service.UserService;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;

public class AuthController extends Controller {
    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String REDIRECT_PATH = "/";
    
    public static User getUser() {
        return (User)Http.Context.current().args.get("user");
    }
    
    /**
     * Register
     *
     * @return Result
     */
    @Transactional
    public Result register() {
        Form<Register> registerForm = Form.form(Register.class).bindFromRequest();
        
        if (registerForm.hasErrors()) {
            return badRequest(registerForm.errorsAsJson());
        }
        
        Register register = registerForm.get();
        User user;
        try {
            user = UserService.register(register);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            user = null;
        }
        
        // Login after register
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

    /**
     * Login
     *
     * @return Result
     */
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
    
    /**
     * Logout
     *
     * @return Result
     */
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
    
    public static class Register {
        @Constraints.Required
        public String username;
        
        @Constraints.Required
        @Constraints.Email
        public String             email;

        @Constraints.Required
        public String             password;
        
        @Constraints.Required
        public String             passwordRepeat;

        public String             firstName;
        public String             lastName;

        @Constraints.Required
        public TypeUser           type;
        
        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            if (!UserService.check("email", email).isEmpty()) {
                errors.add(new ValidationError("email", "This e-mail is already registered"));
            }
            if (!UserService.check("username", username).isEmpty()) {
                errors.add(new ValidationError("username", "This username is already registered"));
            }
            if (password != null && passwordRepeat != null && !password.equals(passwordRepeat)) {
                errors.add(new ValidationError("password", "The passwords must be equals"));
                errors.add(new ValidationError("passwordRepeat", "The passwords must be equals"));
            }
            return errors.isEmpty() ? null : errors;
        }
    }
}
