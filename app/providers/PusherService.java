package providers;


import com.pusher.rest.Pusher;
import models.Recipe;
import models.User;
import play.Play;
import play.i18n.Messages;

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
            if (!Play.isTest()) System.err.println(e.getMessage());
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
        if (!recipe.user.equals(user)) {
            String data = "{ \"redirect\": \"/recipes/" + recipe.slug + "\", \"msg\": \"" + Messages.get("pusher.favorite", user.getFullName(), recipe.title) + "\"}";
            sendNotificationToUser(recipe.user, "recipe_favorite", data);
        }
    }

    /**
     * Send the notification when an user comment a recipe
     *
     * @param recipe Recipe
     * @param user   User
     */
    public void notificateComment(Recipe recipe, User user) {
        if (!recipe.user.equals(user)) {
            String data = "{ \"redirect\": \"/recipes/" + recipe.slug + "\", \"msg\": \"" + Messages.get("pusher.comment", user.getFullName(), recipe.title) + "\"}";
            sendNotificationToUser(recipe.user, "recipe_comment", data);
        }
    }

    /**
     * Send the notification when an user reply a comment
     *
     * @param recipe Recipe
     * @param user   User
     * @param owner  User
     */
    public void notificateReply(Recipe recipe, User user, User owner) {
        if (!user.equals(owner)) {
            String data = "{ \"redirect\": \"/recipes/" + recipe.slug + "\", \"msg\": \"" + Messages.get("pusher.reply", user.getFullName(), recipe.title) + "\"}";
            sendNotificationToUser(owner, "comment_reply", data);
        }
    }
}
