package providers;


import com.pusher.rest.Pusher;
import models.Recipe;
import models.User;
import play.Play;

import java.util.Collections;

public class PusherService {
    private Pusher pusher;

    public PusherService() {
        try {
            this.pusher = new Pusher(
                    Play.application().configuration().getString("pusher.appID"),
                    Play.application().configuration().getString("pusher.key"),
                    Play.application().configuration().getString("pusher.secret")
            );
            this.pusher.setCluster("eu");
            this.pusher.setEncrypted(true);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
        }
    }

    public void sendTest() {
        pusher.trigger("test", "test", Collections.singletonMap("message", "hello world"));
    }

    public void sendNotificationToUser(User user, String eventName, Object body) {
        if (pusher != null) {
            pusher.trigger("user_" + user.id, eventName, body);
        }
    }

    /**
     * Send the notification when an user check as favorite a recipe
     *
     * @param recipe Recipe
     * @param user   User
     */
    public void notificateFavorite(Recipe recipe, User user) {
        String data = "{ \"msg\": \"Al usuario " + user.getFullName() + " le ha gustado tu receta '" + recipe.title + "'\"}";
        sendNotificationToUser(recipe.user, "recipe_favorite", data);
    }

    /**
     * Send the notification when an user comment a recipe
     *
     * @param recipe Recipe
     * @param user   User
     */
    public void notificateComment(Recipe recipe, User user) {
        String data = "{ \"msg\": \"El usuario " + user.getFullName() + " ha comentado tu receta '" + recipe.title + "'\"}";
        sendNotificationToUser(recipe.user, "recipe_comment", data);
    }

    /**
     * Send the notification when an user reply a comment
     *
     * @param recipe Recipe
     * @param user   User
     * @param owner  User
     */
    public void notificateReply(Recipe recipe, User user, User owner) {
        String data = "{ \"msg\": \"El usuario " + user.getFullName() + " ha contestado un comentario tuyo en la receta '" + recipe.title + "'\"}";
        sendNotificationToUser(owner, "comment_reply", data);
    }
}
