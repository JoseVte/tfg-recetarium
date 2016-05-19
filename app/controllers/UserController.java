package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Admin;
import middleware.Authenticated;
import models.Recipe;
import models.User;
import models.dao.UserDAO;
import models.enums.TypeUser;
import models.service.RecipeService;
import models.service.UserService;
import play.Logger;
import play.data.Form;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import providers.PusherService;
import views.html.index;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserController extends AbstractCrudController {
    private Form<UserRequest> formModel = Form.form(UserRequest.class);
    private final PusherService pusher;

    @Inject
    public UserController() {
        this.pusher = new PusherService();
    }

    /**
     * Get the index page
     *
     * @return Result
     */
    public Result index() {
        pusher.sendTest();
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    @SuppressWarnings("deprecation")
    public Result list(Integer page, Integer size, String search, String order) {
        List<User> models = UserService.paginate(page - 1, size, search, order);
        Long count = UserService.count(search);
        String[] routesString = new String[3];
        routesString[0] = routes.UserController.list(page - 1, size, search, order).toString();
        routesString[1] = routes.UserController.list(page + 1, size, search, order).toString();
        routesString[2] = routes.UserController.list(page, size, search, order).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, !Objects.equals(search, ""));

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    public Result get(Integer id) {
        User user = UserService.find(id);
        if (user == null) {
            return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.user"), id))));
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
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
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
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-equals", Messages.get("article.male-plural"), "IDs"))));
        }
        User userModel = UserService.update(user.get());
        return util.Json.jsonResult(response(), ok(Json.toJson(userModel)));
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result delete(Integer id) {
        if (UserService.delete(id)) {
            return util.Json.jsonResult(response(), ok(util.Json.generateJsonInfoMessages(Messages.get("info.delete", Messages.get("article.male-single"), Messages.get("field.user"), id))));
        }
        return util.Json.jsonResult(response(), notFound(util.Json.generateJsonErrorMessages(Messages.get("error.not-found", Messages.get("article.male-single"), Messages.get("field.user"), id))));
    }

    /**
     * Toggle favorite the current user into a recipe
     *
     * @param id Integer
     *
     * @return Result
     */
    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result toggleFav(Integer id) {
        ObjectNode data = Json.newObject();
        User user = Json.fromJson(Json.parse(request().username()), User.class);
        Recipe recipe = RecipeService.find(id);

        boolean fav = RecipeService.addFavorite(user.id, id);
        if (!fav) {
            boolean noFav = RecipeService.deleteFavorite(user.id, id);
            if (!noFav) {
                return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
            }
            data.put("fav", false);
        } else {
            pusher.notificateFavorite(recipe, user);
            data.put("fav", true);
        }
        data.put("favorites", RecipeService.countFavorites(id));

        return util.Json.jsonResult(response(), ok(data));
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
        public Integer avatar = null;

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
                errors.add(new ValidationError("id", Messages.get("error.field-no-existing", Messages.get("article.male-single"), "ID", id)));
            }
            if (!dao.where("email", email, id).isEmpty()) {
                errors.add(new ValidationError("email", Messages.get("error.field-existing", "email")));
            }
            if (!dao.where("username", username, id).isEmpty()) {
                errors.add(new ValidationError("username", Messages.get("error.field-existing", "username")));
            }
            if ((id == null || dao.find(id) == null) && (password == null || password.isEmpty())) {
                errors.add(new ValidationError("password", Messages.get("error.required")));
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
