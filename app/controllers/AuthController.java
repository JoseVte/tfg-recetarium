package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Anonymous;
import middleware.Authenticated;
import models.User;
import models.service.FileService;
import play.Play;
import play.i18n.Messages;
import providers.EmailService;
import models.service.UserService;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.libs.mailer.MailerClient;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import util.VerificationToken;

import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class AuthController extends Controller {
    public final static String AUTH_TOKEN_HEADER = "X-AUTH-TOKEN";
    public static final String AUTH_TOKEN_FIELD = "auth_token";
    public static final String PUSHER_KEY = "pusher_key";
    public static final String LANGUAGE_FIELD = "language";
    public static final String REDIRECT_PATH = "/";

    private final EmailService mailer;

    @Inject
    public AuthController(MailerClient mailer) {
        this.mailer = new EmailService(mailer);
    }

    private ObjectNode generateAuthJson(String token, String language) {
        ObjectNode json = Json.newObject();
        json.put(AUTH_TOKEN_FIELD, token);
        json.put(PUSHER_KEY, Play.application().configuration().getString("pusher.key"));
        json.put(LANGUAGE_FIELD, language);
        return json;
    }

    /**
     * Register
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Anonymous.class)
    public Result register() {
        Form<Register> registerForm = Form.form(Register.class).bindFromRequest();

        if (registerForm.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(registerForm.errorsAsJson()));
        }

        try {
            Register register = registerForm.get();
            if (register.language == null && !request().acceptLanguages().isEmpty()) {
                register.language = request().acceptLanguages().get(0).language();
            }
            User user = UserService.register(register);
            if (user == null) {
                return util.Json.jsonResult(response(), unauthorized());
            } else {
                if (mailer.sendRegistrationEmails(user) == null) {
                    return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
                }
                return util.Json.jsonResult(response(), ok());
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }

    }

    /**
     * Login
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Anonymous.class)
    public Result login() {
        Form<Login> loginForm = Form.form(Login.class).bindFromRequest();

        if (loginForm.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(loginForm.errorsAsJson()));
        }

        Login login = loginForm.get();

        User user = UserService.findByEmailAddressAndPassword(login.email, login.password);

        if (user == null || !user.isActive()) {
            return util.Json.jsonResult(response(), unauthorized());
        } else {
            String authToken = UserService.createJWT(user, login.setExpiration);
            return util.Json.jsonResult(response(), ok(generateAuthJson(authToken, user.language)));
        }
    }

    /**
     * Send lost password token
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Anonymous.class)
    public Result sendLostPasswordToken() {
        Form<RecoverPassword> recover = Form.form(RecoverPassword.class).bindFromRequest();
        if (recover.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(recover.errorsAsJson()));
        }
        VerificationToken token;
        String email = recover.get().email;
        User user = UserService.findByEmailAddress(email);
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), "email", email))));
        }
        token = UserService.getActiveLostPasswordToken(user);
        if (token == null) {
            UserService.addVerification(user);
        }
        if (mailer.sendVerificationToken(user) == null) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }
        return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.reset-password"))));
    }

    /**
     * Reset the password from an email and token
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Anonymous.class)
    public Result resetPassword() {
        Form<ResetPassword> reset = Form.form(ResetPassword.class).bindFromRequest();

        if (reset.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(reset.errorsAsJson()));
        }

        User user = UserService.changePassword(reset.get().token, reset.get().password);
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), "token", reset.get().token))));
        } else if (mailer.sendChangedPassword(user) == null) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }

        return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.change-password"))));
    }

    /**
     * Logout
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result logout() {
        return ok();
    }

    /**
     * Check the token and creates a new
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result checkToken() {
        Form<Check> checkForm = Form.form(Check.class).bindFromRequest();
        Check check = checkForm.get();
        User user = UserService.findByEmailAddress(check.email);
        if (user.equals(Json.fromJson(Json.parse(request().username()), User.class))) {
            String authToken = UserService.createJWT(user, check.setExpiration);
            return util.Json.jsonResult(response(), ok(generateAuthJson(authToken, user.language)));
        }
        return unauthorized();
    }

    /**
     * Active the account by token
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Anonymous.class)
    public Result activeToken() {
        Form<ActiveAccount> active = Form.form(ActiveAccount.class).bindFromRequest();

        if (active.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(active.errorsAsJson()));
        }

        User user = UserService.activeAccount(active.get().token);
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), "token", active.get().token))));
        } else if (mailer.sendAccountActivated(user) == null) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }

        return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.account-activated"))));
    }

    /**
     * Get user profile
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    public Result profile() {
        User user = UserService.find(Json.fromJson(Json.parse(request().username()), User.class).id);
        return util.Json.jsonResult(response(), ok(Json.toJson(user)));
    }

    /**
     * Update user profile
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result updateProfile() {
        Form<Profile> profile = Form.form(Profile.class).bindFromRequest();
        if (profile.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(profile.errorsAsJson()));
        }
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        if (profile.get().avatar != null && FileService.find(user.id, profile.get().avatar) == null) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-no-existing", Messages.get("article.male-single"), "avatar", profile.get().avatar))));
        }
        User userModel = UserService.update(user, profile.data());
        return util.Json.jsonResult(response(), ok(Json.toJson(userModel)));
    }

    /***********************/
    /* Request validators */
    /* @author Josrom */

    /***********************/
    public static class RecoverPassword {
        @Constraints.Required
        @Constraints.Email
        public String email;
    }

    public static class Check extends RecoverPassword {
        public boolean setExpiration = true;
    }

    public static class Login extends RecoverPassword {
        @Constraints.Required
        public String password;

        public boolean setExpiration = true;
    }

    public static class Register extends Login {
        @Constraints.Required
        public String username;

        @Constraints.Required
        public String password_repeat;

        public String first_name;
        public String last_name;
        public String language = null;

        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            if (!UserService.where("email", email).isEmpty()) {
                errors.add(new ValidationError("email", Messages.get("error.field-existing", "email")));
            }
            if (!UserService.where("username", username).isEmpty()) {
                errors.add(new ValidationError("username", Messages.get("error.field-existing", "username")));
            }
            if (password != null && password_repeat != null && !password.equals(password_repeat)) {
                errors.add(new ValidationError("password", Messages.get("error.field-equals", Messages.get("article.female-plural"), Messages.get("field.passwords"))));
                errors.add(new ValidationError("password_repeat", Messages.get("error.field-equals", Messages.get("article.female-plural"), Messages.get("field.passwords"))));
            }
            return errors.isEmpty() ? null : errors;
        }

        @Override
        public String toString() {
            return "User [username=" + username + ", email=" + email + ", password=" + password + ", firstName="
                    + first_name + ", lastName=" + last_name + "]";
        }
    }

    public static class Profile {
        public String password;
        public String password_repeat;

        public String first_name;
        public String last_name;
        public Integer avatar = null;
        public String language = null;

        public Profile() { }

        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            if ((password == null ^ password_repeat == null) || (password != null && password_repeat != null && !password.equals(password_repeat))) {
                errors.add(new ValidationError("password", Messages.get("error.field-equals", Messages.get("article.female-plural"), Messages.get("field.passwords"))));
                errors.add(new ValidationError("password_repeat", Messages.get("error.field-equals", Messages.get("article.female-plural"), Messages.get("field.passwords"))));
            }
            return errors.isEmpty() ? null : errors;
        }
    }

    public static class ActiveAccount {
        @Constraints.Required
        public String token;
    }

    public static class ResetPassword {
        @Constraints.Required
        public String password;

        @Constraints.Required
        public String token;

        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<>();
            if (!UserService.validateResetToken(token)) {
                errors.add(new ValidationError("token", Messages.get("error.invalid-token")));
            }
            return errors.isEmpty() ? null : errors;
        }
    }
}
