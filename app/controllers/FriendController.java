package controllers;

import com.fasterxml.jackson.databind.node.ObjectNode;
import middleware.Authenticated;
import models.User;
import models.service.UserService;
import play.data.Form;
import play.data.validation.Constraints;
import play.db.jpa.Transactional;
import play.i18n.Messages;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Security;

import java.util.List;
import java.util.Objects;

public class FriendController extends AbstractController {
    private static Form<FriendRequest> friendForm = Form.form(FriendRequest.class);

    @Transactional(readOnly = true)
    @Security.Authenticated(Authenticated.class)
    @SuppressWarnings("deprecation")
    public Result list(Integer id, Integer page, Integer size, String search, String order) {
        List<User> models = UserService.getFriendsPaginate(id, page - 1, size, search, order);
        Long count = UserService.countFriends(id, search);
        String[] routesString = new String[3];
        routesString[0] = routes.FriendController.list(id, page - 1, size, search, order).toString();
        routesString[1] = routes.FriendController.list(id, page + 1, size, search, order).toString();
        routesString[2] = routes.FriendController.list(id, page, size, search, order).toString();

        ObjectNode result = util.Json.generateJsonPaginateObject(models, count, page, size, routesString, !Objects.equals(search, ""));

        return util.Json.jsonResult(response(), ok(result));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result create(Integer id) {
        Form<FriendRequest> friend = friendForm.bindFromRequest();
        if (!Objects.equals(Json.fromJson(Json.parse(request().username()), User.class).id, id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-equals", Messages.get("article.male-plural"), "IDs"))));
        }
        if (!UserService.addFriend(id, friend.get().id)) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }
        return util.Json.jsonResult(response(), ok(util.Json.generateJsonBooleanInfoMessages("added", true)));
    }

    @Transactional
    @Security.Authenticated(Authenticated.class)
    public Result delete(Integer id, Integer friendId) {
        if (!Objects.equals(Json.fromJson(Json.parse(request().username()), User.class).id, id)) {
            return util.Json.jsonResult(response(), badRequest(util.Json.generateJsonErrorMessages(Messages.get("error.field-equals", Messages.get("article.male-plural"), "IDs"))));
        }
        if (!UserService.deleteFriend(id, friendId)) {
            return util.Json.jsonResult(response(), internalServerError(util.Json.generateJsonErrorMessages(Messages.get("error.server"))));
        }
        return util.Json.jsonResult(response(), ok(util.Json.generateJsonBooleanInfoMessages("deleted", true)));
    }

    public static class FriendRequest {
        @Constraints.Required
        public Integer id;
    }
}
