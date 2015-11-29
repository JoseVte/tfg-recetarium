package controllers;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.node.ObjectNode;

import models.TypeUser;
import models.User;
import models.service.EmailService;
import models.service.UserService;
import play.libs.mailer.MailerClient;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Security;
import util.VerificationToken;

public class AuthController extends Controller {
    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN = "auth_token";
    public static final String REDIRECT_PATH = "/";
    
    private final MailerClient mailer;

    @Inject
    public AuthController(MailerClient mailer) {
      this.mailer = mailer;
    }
    
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
            return util.Json.jsonResult(response(), badRequest(registerForm.errorsAsJson()));
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
            return util.Json.jsonResult(response(), unauthorized());
        } else {
            String authToken = UserService.createJWT(user);
            ObjectNode authTokenJson = Json.newObject();
            authTokenJson.put(AUTH_TOKEN, authToken);
            response().setCookie(AUTH_TOKEN, authToken);
            return util.Json.jsonResult(response(), ok(authTokenJson));
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
            return util.Json.jsonResult(response(), badRequest(loginForm.errorsAsJson()));
        }

        Login login = loginForm.get();

        User user = UserService.findByEmailAddressAndPassword(login.email, login.password);

        if (user == null) {
            return util.Json.jsonResult(response(), unauthorized());
        } else {
            String authToken = UserService.createJWT(user);
            ObjectNode authTokenJson = Json.newObject();
            authTokenJson.put(AUTH_TOKEN, authToken);
            response().setCookie(AUTH_TOKEN, authToken);
            return util.Json.jsonResult(response(), ok(authTokenJson));
        }
    }
    
    /**
     * Send lost password token
     *
     * @return Result
     */
    @Transactional
    public Result sendLostPasswordToken() {
        Form<RecoverPassword> recover = Form.form(RecoverPassword.class).bindFromRequest();
        VerificationToken token = null;
        String email = recover.get().email;
        User user = UserService.findByEmailAddress(email);
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found email " + email)));
        }
        token = UserService.getActiveLostPasswordToken(user);
        if (token == null) {
            token = UserService.addVerification(user);
        }
        new EmailService(mailer).sendVerificationToken(user);
        return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Reset password email sent")));
    }
    
    /**
     * Reset the password from an email and token
     *
     * @return Result
     */
    @Transactional
    public Result resetPassword() {
        Form<ResetPassword> reset = Form.form(ResetPassword.class).bindFromRequest();
        
        if (reset.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(reset.errorsAsJson()));
        }
        
        UserService.changePassword(reset.get().email, reset.get().password);
        
        return ok();
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
        return ok();
    }
    
    /***********************/
    /* Request validators  */
    /* @author Josrom      */
    /***********************/
    public static class RecoverPassword {
        @Constraints.Required
        @Constraints.Email
        public String email;
    }
    
    public static class Login extends RecoverPassword {
        @Constraints.Required
        public String password;

    }
    
    public static class Register extends Login {
        @Constraints.Required
        public String username;
        
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
    
    public static class ResetPassword extends Login {
        @Constraints.Required
        public String token;
        
        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<>();
            if (!UserService.validateResetToken(email, token)) {
                errors.add(new ValidationError("token", "Invalid token"));
            }
            return errors.isEmpty() ? null : errors;
        }
    }
}
