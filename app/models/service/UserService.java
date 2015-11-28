package models.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import controllers.AuthController.Register;
import models.Recipe;
import models.User;
import models.dao.UserDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import util.VerificationToken;

public class UserService {
    private static UserDAO userDAO;
    static {
        userDAO = new UserDAO();
    }

    /**
     * Create an user
     *
     * @param User data
     *
     * @return User
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static User create(User data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return userDAO.create(data);
    }

    /**
     * Update an user
     *
     * @param User data
     *
     * @return User
     */
    public static User update(User data) {
        return userDAO.update(data);
    }

    /**
     * Find an user by id
     *
     * @param Integer id
     *
     * @return User
     */
    public static User find(Integer id) {
        return userDAO.find(id);
    }

    /**
     * Delete an user by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        User user = userDAO.find(id);
        if (user != null) {
            userDAO.delete(user);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all users
     *
     * @return List<User>
     */
    public static List<User> all() {
        return userDAO.all();
    }

    /**
     * Get the page of users
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<User>
     */
    public static List<User> paginate(Integer page, Integer size) {
        return userDAO.paginate(page, size);
    }

    /**
     * Get the number of total of users
     *
     * @return Long
     */
    public static Long count() {
        return userDAO.count();
    }
    
    /**
     * Check the value of field is used
     *
     * @param field
     * @param value
     *
     * @return List<User>
     */
    public static List<User> check(String field, String value) {
        return userDAO.check(field, value);
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
    public static User register(Register register) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return userDAO.register(register);
    }
    
    /**
     * Find an user by email
     *
     * @param email
     *
     * @return User
     */
    public static User findByEmailAddress(String email) {
        return userDAO.findByEmailAddress(email);
    }

    /**
     * Find an user by email and password
     *
     * @param email
     * @param password
     *
     * @return User
     */
    public static User findByEmailAddressAndPassword(String email, String password) {
        return userDAO.findByEmailAddressAndPassword(email, password);
    }
    
    /**
     * Check the auth token
     *
     * @param authToken
     *
     * @return User
     */
    public static User checkJWT(String authToken) {
        return userDAO.checkJWT(authToken);
    }

    /**
     * Create a token for the user
     *
     * @param user
     *
     * @return String
     */
    public static String createJWT(User user) {
        return userDAO.createJWT(user);
    }
    
    public static VerificationToken getActiveLostPasswordToken(User user) {
        VerificationToken token = userDAO.getLostPasswordToken(user);
        if (token.hasExpired() || token.isVerified()) return null;
        return token;
    }
    
    public static VerificationToken addVerification(User user) {
        VerificationToken token = new VerificationToken();
        user.lostPassToken = token.getToken();
        user.lostPassExpire = token.getExpiryDate();
        userDAO.update(user);
        return token;
    }

    /**
     * Add new friend to an user
     *
     * @param user
     * @param friend
     *
     * @return boolean
     */
    public static boolean addFriend(Integer userId, Integer friendId) {
        if (userId != friendId) {
            User user = userDAO.find(userId);
            User friend = userDAO.find(friendId);
            if (user != null && friend != null) {
                Friend friendship = new Friend(user, friend);
                if (!user.myFriends.contains(friendship)) {
                    UserDAO.addFriend(user, friend);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Delete a friendship relation
     *
     * @param user
     * @param friend
     *
     * @return boolean
     */
    public static boolean deleteFriend(Integer userId, Integer friendId) {
        if (userId != friendId) {
            User user = userDAO.find(userId);
            User friend = userDAO.find(friendId);
            if (user != null && friend != null) {
                Friend friendship = new Friend(user, friend);
                if (user.myFriends.contains(friendship)) {
                    UserDAO.deleteFriend(user, friend);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Add a recipe as a favorite
     *
     * @param user
     * @param recipe
     *
     * @return boolean
     */
    public static boolean addFavorite(Integer userId, Integer recipeId) {
        User user = userDAO.find(userId);
        Recipe recipe = RecipeService.find(recipeId);
        if (user != null && recipe != null) {
            Favorite fav = new Favorite(user, recipe);
            if (!user.recipesFavorites.contains(fav)) {
                UserDAO.addFavorite(user, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a recipe favorite
     *
     * @param user
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteFavorite(Integer userId, Integer recipeId) {
        User user = userDAO.find(userId);
        Recipe recipe = RecipeService.find(recipeId);
        if (user != null && recipe != null) {
            Favorite fav = new Favorite(user, recipe);
            if (user.recipesFavorites.contains(fav)) {
                UserDAO.deleteFavorite(user, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Add a rating of a recipe
     *
     * @param user
     * @param recipe
     * @param value
     *
     * @return boolean
     */
    public static boolean addRating(Integer userId, Integer recipeId, double value) {
        User user = userDAO.find(userId);
        Recipe recipe = RecipeService.find(recipeId);
        if (user != null && recipe != null && value >= 0.0 && value <= 5.0) {
            Rating fav = new Rating(user, recipe);
            if (!user.ratings.contains(fav)) {
                UserDAO.addRating(user, recipe, value);
                return true;
            }
        }
        return false;
    }

    /**
     * Update a rating of a recipe
     *
     * @param user
     * @param recipe
     * @param value
     *
     * @return boolean
     */
    public static boolean updateRating(Integer userId, Integer recipeId, double value) {
        User user = userDAO.find(userId);
        Recipe recipe = RecipeService.find(recipeId);
        if (user != null && recipe != null && value >= 0.0 && value <= 5.0) {
            Rating fav = new Rating(user, recipe);
            if (user.ratings.contains(fav)) {
                UserDAO.updateRating(user, recipe, value);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a rating of a recipe
     *
     * @param user
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteRating(Integer userId, Integer recipeId) {
        User user = userDAO.find(userId);
        Recipe recipe = RecipeService.find(recipeId);
        if (user != null && recipe != null) {
            Rating fav = new Rating(user, recipe);
            if (user.ratings.contains(fav)) {
                UserDAO.deleteRating(user, recipe);
                return true;
            }
        }
        return false;
    }
}
