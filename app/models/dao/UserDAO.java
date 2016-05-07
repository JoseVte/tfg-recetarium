package models.dao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AuthController.Register;
import models.Recipe;
import models.User;
import models.base.CrudDAO;
import models.enums.TypeUser;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import org.jose4j.lang.JoseException;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.Json;
import util.Encryptation;
import util.VerificationToken;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public class UserDAO extends CrudDAO<User> {
    public UserDAO() {
        super(User.class);
    }

    /**
     * Get the page of models order by field
     *
     * @param search String
     * @param page   Integer
     * @param size   Integer
     * @param order  String
     *
     * @return List<User>
     */
    public List<User> paginate(Integer page, Integer size, String search, String order) {
        return JPA.em().createQuery("SELECT u FROM " + TABLE + " u WHERE " + User.Search(search) + " ORDER BY " + order, User.class).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    /**
     * Count the all user with search parameter
     *
     * @param search String
     *
     * @return Long
     */
    public Long count(String search) {
        return JPA.em().createQuery("SELECT count(u) FROM " + TABLE + " u WHERE " + User.Search(search), Long.class).getSingleResult();
    }

    /**
     * Validates a password using a hash.
     *
     * @param password    the password to check
     * @param correctHash the hash of the valid password
     *
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(String password, String correctHash) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return Encryptation.check(password, correctHash);
    }

    /**
     * Get the page of models order by field
     *
     * @param userId Integer
     * @param search String
     * @param page   Integer
     * @param size   Integer
     * @param order  String
     *
     * @return List<User>
     */
    public List<User> getFriendsPaginate(Integer userId, Integer page, Integer size, String search, String order) {
        return JPA.em().createQuery("SELECT u FROM " + Friend.class.getName() + " f JOIN f.friend u WHERE f.user = " + userId
                + " AND " + User.Search(search) + " ORDER BY u." + order, User.class).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    public Long countFriends(Integer userId, String search) {
        return JPA.em().createQuery("SELECT count(u) FROM " + Friend.class.getName() + " f JOIN f.friend u WHERE f.user = " + userId
                + " AND (u.username LIKE '%" + search + "%' OR u.email LIKE '%" + search + "%' OR u.firstName LIKE '%" + search +
                "%' OR u.lastName LIKE '%" + search + "%')", Long.class).getSingleResult();
    }

    /**
     * Add new friend to an user
     *
     * @param user   User
     * @param friend User
     */
    public static void addFriend(User user, User friend) {
        Friend friendship = new Friend(user, friend);
        JPA.em().persist(friendship);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(friend);
    }

    /**
     * Delete a friendship relation
     *
     * @param user   User
     * @param friend User
     */
    public static void deleteFriend(User user, User friend) {
        Friend friendship = JPA.em().createQuery("SELECT m FROM " + Friend.class.getName() + " m WHERE user_id = "
                + user.id + " AND friend_id = " + friend.id, Friend.class).getSingleResult();
        JPA.em().remove(friendship);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(friend);
    }

    /**
     * Add a recipe as a favorite
     *
     * @param user   User
     * @param recipe User
     */
    public static void addFavorite(User user, Recipe recipe) {
        Favorite fav = new Favorite(user, recipe);
        JPA.em().persist(fav);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete a recipe favorite
     *
     * @param user   User
     * @param recipe Recipe
     */
    public static void deleteFavorite(User user, Recipe recipe) {
        Favorite fav = JPA.em().createQuery("SELECT m FROM " + Favorite.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id, Favorite.class).getSingleResult();
        JPA.em().remove(fav);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Add a rating of a recipe
     *
     * @param user   User
     * @param recipe Recipe
     * @param value  double
     */
    public static void addRating(User user, Recipe recipe, double value) {
        Rating rating = new Rating(user, recipe, value);
        JPA.em().persist(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Update a rating of a recipe
     *
     * @param user   User
     * @param recipe Recipe
     */
    public static void updateRating(User user, Recipe recipe, double value) {
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = " + user.id
                + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
        rating.rating = value;
        JPA.em().merge(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete a rating of a recipe
     *
     * @param user   User
     * @param recipe Recipe
     */
    public static void deleteRating(User user, Recipe recipe) {
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = " + user.id
                + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
        JPA.em().remove(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Register an user
     *
     * @param register Register
     *
     * @return User
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public User register(Register register) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = new User(register.username, register.email, register.password, register.first_name,
                register.last_name, TypeUser.COMUN);
        return this.create(user);
    }

    /**
     * Where clause
     *
     * @param field      String
     * @param value      Object
     * @param id         Integer
     * @param comparison String
     *
     * @return List<User>
     */
    public List<User> where(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id", User.class).getResultList();
    }

    /**
     * Where clause
     *
     * @param field String
     * @param value Object
     * @param id    Integer
     *
     * @return List<User>
     */
    public List<User> where(String field, Object value, Integer id) {
        return where(field, value, id, "=");
    }

    /**
     * Where clause
     *
     * @param field String
     * @param value Object
     *
     * @return List<User>
     */
    public List<User> where(String field, Object value) {
        return where(field, value, 0, "=");
    }

    /**
     * Create a token for the user
     *
     * @param user          User
     * @param setExpiration boolean
     *
     * @return String
     */
    @SuppressWarnings("deprecation")
    public String createJWT(User user, boolean setExpiration) {
        try {
            ObjectMapper json = new ObjectMapper();
            ObjectNode object = json.createObjectNode();
            object.put("user", Json.toJson(user));
            object.put("setExpiration", setExpiration);

            return util.Json.createJwt(object.toString(), setExpiration);
        } catch (JoseException e) {
            Logger.error(e.getMessage());
            return "";
        }
    }

    /**
     * Check the auth token
     *
     * @param jwt String
     *
     * @return User
     */
    public User checkJWT(String jwt) {
        if (jwt == null) return null;

        try {
            JsonNode json = Json.parse(util.Json.checkJwt(jwt));
            if (!json.has("user")) throw new Exception("Token malformed");

            return Json.fromJson(json.get("user"), User.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get the token valid of an user
     *
     * @param user User
     *
     * @return VerificationToken
     */
    public VerificationToken getLostPasswordToken(User user) {
        if (user != null && user.lostPassToken != null && !user.lostPassToken.isEmpty()) {
            return new VerificationToken(user.lostPassToken, user.lostPassExpire);
        }
        return null;
    }

    /**
     * Find an user by email
     *
     * @param email String
     *
     * @return User
     */
    public User findByEmailAddress(String email) {
        if (email == null) return null;
        try {
            return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE email = '" + email + "'", User.class)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Find an user by email and password
     *
     * @param email    String
     * @param password String
     *
     * @return User
     */
    public User findByEmailAddressAndPassword(String email, String password) {
        if (email == null || password == null) return null;

        try {
            User user = findByEmailAddress(email);
            if (validatePassword(password, user.password)) return user;
            return null;
        } catch (Exception e) {
            return null;
        }
    }

    public void addRecipeCount(User user) {
        JPA.em().createQuery("UPDATE " + TABLE + " m SET num_recipes = '" + user.numRecipes + 1 + "' WHERE id = " + user.id).executeUpdate();
    }
}
