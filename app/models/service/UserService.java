package models.service;

import controllers.AuthController.Register;
import controllers.UserController.UserRequest;
import models.Recipe;
import models.User;
import models.dao.UserDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import util.Encryptation;
import util.VerificationToken;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserService {
    private static UserDAO userDAO;

    static {
        userDAO = new UserDAO();
    }

    /**
     * Create an user
     *
     * @param data User
     *
     * @return User
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static User create(User data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return userDAO.create(data);
    }

    /**
     * Create an user
     *
     * @param data UserRequest
     *
     * @return User
     *
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static User create(UserRequest data) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return userDAO.create(new User(data));
    }

    /**
     * Update an user
     *
     * @param data User
     *
     * @return User
     */
    public static User update(User data) {
        data.password = Encryptation.createHash(data.password);
        return userDAO.update(data);
    }

    /**
     * Update an user
     *
     * @param data UserRequest
     *
     * @return User
     */
    public static User update(UserRequest data) {
        return userDAO.update(new User(data));
    }

    /**
     * Update an user
     *
     * @param user User
     * @param data Map<String, String>
     *
     * @return User
     */
    public static User update(User user, Map<String, String> data) {
        user.firstName = data.get("first_name");
        user.lastName = data.get("last_name");
        if (data.get("avatar") != null) user.avatar = FileService.find(user, Integer.valueOf(data.get("avatar")));
        if (data.get("password") != null && !data.get("password").isEmpty())
            user.password = Encryptation.createHash(data.get("password"));
        return userDAO.update(user);
    }

    /**
     * Find an user by id
     *
     * @param id Integer
     *
     * @return User
     */
    public static User find(Integer id) {
        return userDAO.find(id);
    }

    /**
     * Delete an user by id
     *
     * @param id Integer
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
     * @param page Integer
     * @param size Integer
     *
     * @return List<User>
     */
    public static List<User> paginate(Integer page, Integer size) {
        return paginate(page, size, "", "id");
    }

    /**
     * Get the page of users
     *
     * @param page Integer
     * @param size Integer
     * @param search String
     * @param order String
     *
     * @return List<User>
     */
    public static List<User> paginate(Integer page, Integer size, String search, String order) {
        return userDAO.paginate(page, size, search, order);
    }

    /**
     * Get the number of total of users
     *
     * @return Long
     */
    public static Long count() {
        return count("");
    }

    /**
     * Get the number of total of users
     *
     * @param search String
     *
     * @return Long
     */
    public static Long count(String search) {
        return userDAO.count(search);
    }

    /**
     * Check the value of field is used
     *
     * @param field String
     * @param value String
     *
     * @return List<User>
     */
    public static List<User> where(String field, String value) {
        return userDAO.where(field, value);
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
    public static User register(Register register) throws NoSuchAlgorithmException, InvalidKeySpecException {
        User user = userDAO.register(register);
        addVerificationEmail(user);
        return user;
    }

    /**
     * Find an user by email
     *
     * @param email String
     *
     * @return User
     */
    public static User findByEmailAddress(String email) {
        return userDAO.findByEmailAddress(email);
    }

    /**
     * Find an user by email and password
     *
     * @param email    String
     * @param password String
     *
     * @return User
     */
    public static User findByEmailAddressAndPassword(String email, String password) {
        return userDAO.findByEmailAddressAndPassword(email, password);
    }

    /**
     * Check the auth token
     *
     * @param authToken String
     *
     * @return User
     */
    public static User checkJWT(String authToken) {
        return userDAO.checkJWT(authToken);
    }

    /**
     * Create a token for the user
     *
     * @param user          User
     * @param setExpiration boolean
     *
     * @return String
     */
    public static String createJWT(User user, boolean setExpiration) {
        return userDAO.createJWT(user, setExpiration);
    }

    /**
     * Get the token valid of an user
     *
     * @param user User
     *
     * @return VerificationToken
     */
    public static VerificationToken getActiveLostPasswordToken(User user) {
        VerificationToken token = userDAO.getLostPasswordToken(user);
        if (token == null || token.hasExpired()) return null;
        return token;
    }

    /**
     * Add new token for the user
     *
     * @param user User
     *
     * @return VerificationToken
     */
    public static VerificationToken addVerification(User user) {
        VerificationToken token = new VerificationToken();
        user.lostPassToken = token.getToken();
        user.lostPassExpire = token.getExpiryDate();
        userDAO.update(user);
        return token;
    }

    /**
     * Active the account
     *
     * @param token String
     *
     * @return User
     */
    public static User activeAccount(String token) {
        return userDAO.activeAccount(token);
    }

    /**
     * Add new token for validation email in the user
     *
     * @param user User
     *
     * @return VerificationToken
     */
    public static VerificationToken addVerificationEmail(User user) {
        VerificationToken token = new VerificationToken();
        user.validationEmailToken = token.getToken();
        userDAO.update(user);
        return token;
    }

    /**
     * Add new token for the user
     *
     * @param token String
     *
     * @return boolean
     */
    public static boolean validateResetToken(String token) {
        if (token == null) return false;
        User user = userDAO.findBy("lost_pass_token", token);
        VerificationToken tokenDB = userDAO.getLostPasswordToken(user);
        return !(user == null || tokenDB == null || !Objects.equals(token, tokenDB.getToken()) || tokenDB.hasExpired());
    }

    /**
     * Change the password of the user
     *
     * @param token    String
     * @param password String
     *
     * @return User
     */
    public static User changePassword(String token, String password) {
        User user = userDAO.findBy("lost_pass_token", token);
        if (user != null) {
            user.password = Encryptation.createHash(password);
            user.lostPassExpire = null;
            user.lostPassToken = null;
            user = userDAO.update(user);
        }
        return user;
    }

    /**
     * Get all friends by user paginate, searched and ordered
     *
     * @param userId Integr
     * @param page   Integer
     * @param size   Integer
     * @param search String
     * @param order  String
     *
     * @return List<User>
     */
    public static List<User> getFriendsPaginate(Integer userId, Integer page, Integer size, String search, String order) {
        return userDAO.getFriendsPaginate(userId, page, size, search, order);
    }

    /**
     * Count all friend by user
     *
     * @param userId Integer
     * @param search String
     *
     * @return Long
     */
    public static Long countFriends(Integer userId, String search) {
        return userDAO.countFriends(userId, search);
    }

    /**
     * Add new friend to an user
     *
     * @param userId   Integer
     * @param friendId Integer
     *
     * @return boolean
     */
    public static boolean addFriend(Integer userId, Integer friendId) {
        if (!Objects.equals(userId, friendId)) {
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
     * @param userId   Integer
     * @param friendId Integer
     *
     * @return boolean
     */
    public static boolean deleteFriend(Integer userId, Integer friendId) {
        if (!Objects.equals(userId, friendId)) {
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
     * @param userId   Integer
     * @param recipeId Integer
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
     * @param userId   Integer
     * @param recipeId Integer
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
     * @param userId   Integer
     * @param recipeId Integer
     * @param value    double
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
     * @param userId   Integer
     * @param recipeId Integer
     * @param value    double
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
     * @param userId   Integer
     * @param recipeId Integer
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

    /**
     * Updates the count of recipes for an user
     *
     * @param user User
     */
    public static void addRecipeCount(User user){
        userDAO.addRecipeCount(user);
    }
}
