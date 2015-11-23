package models.dao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.UUID;

import models.Recipe;
import models.User;
import models.base.CrudDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import play.db.jpa.JPA;
import util.Encryptation;

public class UserDAO extends CrudDAO<User> {
    public UserDAO() {
        super(User.class);
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
    public List<User> check(String field, Object value, Integer id, String comparison) {
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
    public List<User> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
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
    public String createToken(User user) {
        user.authToken = UUID.randomUUID().toString();
        JPA.em().flush();
        JPA.em().refresh(user);
        return user.authToken;
    }

    public void deleteAuthToken(User user) {
        user.authToken = null;
        JPA.em().flush();
        JPA.em().refresh(user);
    }
    
    /**
     * Find an user by auth token
     *
     * @param authToken
     *
     * @return User
     */
    public User findByAuthToken(String authToken) {
        if (authToken == null) return null;

        try  {
            return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE auth_token = '" + authToken + "'", User.class).getSingleResult();
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

        try  {
            User user = JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE email = '" + email + "'", User.class).getSingleResult();
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
        Friend friendship = JPA.em().createQuery("SELECT m FROM " + Friend.class.getName()
                + " m WHERE user_id = " + user.id + " AND friend_id = " + friend.id, Friend.class).getSingleResult();
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
        Favorite fav = JPA.em().createQuery("SELECT m FROM " + Favorite.class.getName()
                + " m WHERE user_id = " + user.id + " AND recipe_id = " + recipe.id, Favorite.class).getSingleResult();
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
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
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
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
        JPA.em().remove(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }
}
