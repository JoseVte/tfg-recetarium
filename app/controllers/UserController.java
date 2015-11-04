package controllers;

import java.util.List;

import play.*;
import play.mvc.*;
import play.libs.Json;
import play.libs.Json.*;
import play.data.Form;
import play.db.jpa.*;

import models.*;
import models.service.UserService;
import views.html.*;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class UserController extends Controller {
    static Form<User> userForm = Form.form(User.class);

    /**
     * Add the content-type json to response
     *
     * @param Result httpResponse
     *
     * @return Result
     */
    public Result jsonResult(Result httpResponse) {
        response().setContentType("application/json; charset=utf-8");
        return httpResponse;
    }

    /**
     * Get the index page
     *
     * @return Result
     */
    public Result index() {
        return ok(index.render("API REST for JAVA Play Framework"));
    }

    /**
     * Get the users with pagination
     *
     * @param Integer page
     * @param Integer size
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result list(Integer page, Integer size) {
        List<User> models = UserService.paginate(page - 1, size);
        Long count = UserService.count();

        ObjectNode result = Json.newObject();
        result.put("data", Json.toJson(models));
        result.put("total", count);
        if (page > 1) result.put("link-prev", routes.UserController.list(page - 1, size).toString());
        if (page * size < count) result.put("link-next", routes.UserController.list(page + 1, size).toString());
        result.put("link-self", routes.UserController.list(page, size).toString());

        return jsonResult(ok(result));
    }

    /**
     * Get one user by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional(readOnly = true)
    public Result get(Integer id) {
        User user = UserService.find(id);
        if (user == null) {
            ObjectNode result = Json.newObject();
            result.put("error", "Not found " + id);
            return jsonResult(notFound(result));
        }
        return jsonResult(ok(Json.toJson(user)));
    }

    /**
     * Create an user with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result create() {
        Form<User> user = userForm.bindFromRequest();
        if (user.hasErrors()) {
            return jsonResult(badRequest(user.errorsAsJson()));
        }
        User newUser = UserService.create(user.get());
        return jsonResult(created(Json.toJson(newUser)));
    }

    /**
     * Update an user with the data of request
     *
     * @return Result
     */
    @Transactional
    public Result update() {
        Form<User> user = userForm.bindFromRequest();
        if (user.hasErrors()) {
            return jsonResult(badRequest(user.errorsAsJson()));
        }
        User updatedUser = UserService.update(user.get());
        return jsonResult(ok(Json.toJson(updatedUser)));
    }

    /**
     * Delete an user by id
     *
     * @param Integer id
     *
     * @return Result
     */
    @Transactional
    public Result delete(Integer id) {
        if (UserService.delete(id)) {
            ObjectNode result = Json.newObject();
            result.put("msg", "Deleted " + id);
            return jsonResult(ok(result));
        }
        ObjectNode result = Json.newObject();
        result.put("error", "Not found " + id);
        return jsonResult(notFound(result));
    }
}
