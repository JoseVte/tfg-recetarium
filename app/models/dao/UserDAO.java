package models.dao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.jose4j.lang.JoseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AuthController.Register;
import models.Recipe;
import models.TypeUser;
import models.User;
import models.base.CrudDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import play.Logger;
import play.db.jpa.JPA;
import play.libs.Json;
import util.Encryptation;
import util.VerificationToken;

public class UserDAO extends CrudDAO<User> {
    public UserDAO() {
        super(User.class);
    }

    /**
     * Register an user
     *
     * @param Register register
     *
     * @return User
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public User register(Register register) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = new User(register.username, register.email, register.password, register.firstName,
                register.lastName, TypeUser.COMUN);
        return this.create(user);
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     * @param String comparison
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
     * @param String field
     * @param Object value
     * @param Integer id
     *
     * @return List<User>
     */
    public List<User> where(String field, Object value, Integer id) {
        return where(field, value, id, "=");
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     *
     * @return List<User>
     */
    public List<User> where(String field, Object value) {
        return where(field, value, 0, "=");
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param correctHash the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(String password, String correctHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        return validatePassword(password.toCharArray(), correctHash);
    }

    /**
     * Validates a password using a hash.
     *
     * @param password the password to check
     * @param correctHash the hash of the valid password
     * @return true if the password is correct, false if not
     */
    public static boolean validatePassword(char[] password, String correctHash)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Decode the hash into its parameters
        String[] params = correctHash.split(":");
        int iterations = Integer.parseInt(params[Encryptation.ITERATION_INDEX]);
        byte[] salt = Encryptation.fromHex(params[Encryptation.SALT_INDEX]);
        byte[] hash = Encryptation.fromHex(params[Encryptation.PBKDF2_INDEX]);

        // Compute the hash of the provided password, using the same salt,
        // iteration count, and hash length
        byte[] testHash = Encryptation.pbkdf2(password, salt, iterations, hash.length);

        // Compare the hashes in constant time. The password is correct if
        // both hashes match.
        return Encryptation.slowEquals(hash, testHash);
    }

    /**
     * Create a token for the user
     *
     * @param user
     *
     * @return String
     */
    @SuppressWarnings("deprecation")
    public String createJWT(User user) {
        try {
            ObjectMapper json = new ObjectMapper();
            ObjectNode object = json.createObjectNode();
            object.put("user", Json.toJson(user));

            return util.Json.createJwt(object.toString());
        } catch (JoseException e) {
            Logger.error(e.getMessage());
            return new String();
        }
    }

    /**
     * Check the auth token
     *
     * @param jwt
     *
     * @return User
     */
    public User checkJWT(String jwt) {
        if (jwt == null) return null;

        try {
            JsonNode json = Json.parse(util.Json.checkJwt(jwt));
            if (!json.has("user")) throw new Exception("Token malformed");
            User user = Json.fromJson(json.get("user"), User.class);

            jwt = this.createJWT(user);

            return user;
        } catch (Exception e) {
            Logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Get the token valid of an user
     *
     * @param user
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
     * @param email
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
     * @param email
     * @param password
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

    /**
     * Add new friend to an user
     *
     * @param user
     * @param friend
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
     * @param user
     * @param friend
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
     * @param user
     * @param recipe
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
     * @param user
     * @param recipe
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
     * @param user
     * @param recipe
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
     * @param user
     * @param recipe
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
     * @param user
     * @param recipe
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
}
