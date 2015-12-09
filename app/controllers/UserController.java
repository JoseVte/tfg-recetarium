package controllers;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import middleware.Admin;
import models.User;
import models.service.UserService;
import play.Logger;
import play.data.Form;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;
import views.html.*;

public class UserController extends AbstractController {
    Form<User> formModel = Form.form(User.class);

    /**
     * Get the index page
     *
     * @return Result
     */
    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Admin.class)
    @SuppressWarnings("deprecation")
    public Result list(Integer page, Integer size) {
        List<User> models = UserService.paginate(page - 1, size);
        Long count = UserService.count();
        String[] routesString = new String[3];
        routesString[0] = routes.UserController.list(page - 1, size).toString();
        routesString[1] = routes.UserController.list(page + 1, size).toString();
        routesString[2] = routes.UserController.list(page, size).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString);

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional(readOnly = true)
    @Security.Authenticated(Admin.class)
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
        Form<User> user = formModel.bindFromRequest();
        if (user.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(user.errorsAsJson()));
        }
        try {
            User newUser = UserService.create(user.get());
            return util.Json.jsonResult(response(), created(Json.toJson(newUser)));
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return util.Json.jsonResult(response(),
                    internalServerError(util.Json.generateJsonErrorMessages("Something went wrong")));
        }
    }

    @Transactional
    @Security.Authenticated(Admin.class)
    public Result update(Integer id) {
        Form<User> user = formModel.bindFromRequest();
        if (user.hasErrors()) {
            return util.Json.jsonResult(response(), badRequest(user.errorsAsJson()));
        }
        User userModel = user.get();
        userModel = UserService.update(userModel);
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
}
