package providers;


import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pusher.rest.Pusher;
import models.Recipe;
import models.User;
import play.Play;
import play.libs.Json;

import java.util.Collections;

public class PusherService {
    private final Pusher pusher;

    public PusherService() {
        this.pusher = new Pusher(
                Play.application().configuration().getString("pusher.appID"),
                Play.application().configuration().getString("pusher.key"),
                Play.application().configuration().getString("pusher.secret")
        );
        this.pusher.setCluster("eu");
        this.pusher.setEncrypted(true);
    }

    public void sendTest() {
        pusher.trigger("test", "test", Collections.singletonMap("message", "hello world"));
    }

    public void sendNotificationToUser(User user, String eventName, Object body) {
        pusher.trigger("user_" + user.id, eventName, body);
    }

    public void notificateFavorite(Recipe recipe, User user) {
        ObjectNode data = Json.newObject();
        data.put("msg", "Al usuario " + user.getFullName() + " le ha gustado tu receta '" + recipe.title + "'");
        sendNotificationToUser(recipe.user, "recipe_favorite", data);
    }
}
