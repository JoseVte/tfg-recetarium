package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Admin;
import middleware.Authenticated;
import models.User;
import models.dao.UserDAO;
import models.enums.TypeUser;
import models.service.UserService;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserController extends AbstractController {
    private Form<UserRequest> formModel = Form.form(UserRequest.class);

    /**
     * Get the index page
     *
     * @return Result
     */
    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    @SuppressWarnings("deprecation")
    public Result list(Integer page, Integer size, String search) {
        List<User> models = UserService.paginate(page - 1, size);
        Long count = UserService.count();
        String[] routesString = new String[3];
        routesString[0] = routes.UserController.list(page - 1, size, search).toString();
        routesString[1] = routes.UserController.list(page + 1, size, search).toString();
        routesString[2] = routes.UserController.list(page, size, search).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, !Objects.equals(search, ""));

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    public Result get(Integer id) {
        User user = UserService.find(id);
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
        }
        return util.Json.jsonResult(response(), ok(Json.toJson(user)));
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result create() {
        Form<UserRequest> user = formModel.bindFromRequest();
        if (user.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(user.errorsAsJson()));
        }
        try {
            User newUser = UserService.create(user.get());
            return util.Json.jsonResult(response(), created(Json.toJson(newUser)));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
        }
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result update(Integer id) {
        Form<UserRequest> user = formModel.bindFromRequest();
        if (user.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(user.errorsAsJson()));
        }
        if (!Objects.equals(user.get().id, id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages("The IDs don't coincide")));
        }
        User userModel = UserService.update(user.get());
        return util.Json.jsonResult(response(), ok(Json.toJson(userModel)));
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result delete(Integer id) {
        if (UserService.delete(id)) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages("Deleted " + id)));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages("Not found " + id)));
    }

    public static class UserRequest {
        public Integer id = null;

        @Constraints.Required
        public String username;

        @Constraints.Required
        @Constraints.Email
        public String email;

        public String password;
        public String first_name;
        public String last_name;

        @Constraints.Required
        public TypeUser type;

        @JsonIgnore
        private UserDAO dao;

        public UserRequest() {
            dao = new UserDAO();
        }

        public List<ValidationError> validate() {
            List<ValidationError> errors = new ArrayList<ValidationError>();
            if (id != null && dao.find(id) == null) {
                errors.add(new ValidationError("id", "This user doesn't exist"));
            }
            if (!dao.where("email", email, id).isEmpty()) {
                errors.add(new ValidationError("email", "This e-mail is already registered"));
            }
            if (!dao.where("username", username, id).isEmpty()) {
                errors.add(new ValidationError("username", "This username is already registered"));
            }
            if ((id == null || dao.find(id) == null) && (password == null || password.isEmpty())) {
                errors.add(new ValidationError("password", "This field is required"));
            }
            return errors.isEmpty() ? null : errors;
        }

        @Override
        public String toString() {
            return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
                    + ", firstName=" + first_name + ", lastName=" + last_name + ", type=" + type.toString() + "]";
        }
    }
}
