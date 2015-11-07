package models.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import models.Recipe;
import models.User;
import models.dao.RecipeDAO;
import models.dao.UserDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;

public class UserService {
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
        return UserDAO.create(data);
    }

    /**
     * Update an user
     *
     * @param User data
     *
     * @return User
     */
    public static User update(User data) {
        return UserDAO.update(data);
    }

    /**
     * Find an user by id
     *
     * @param Integer id
     *
     * @return User
     */
    public static User find(Integer id) {
        return UserDAO.find(id);
    }

    /**
     * Delete an user by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        User user = UserDAO.find(id);
        if (user != null) {
            UserDAO.delete(user);
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
        return UserDAO.all();
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
        return UserDAO.paginate(page, size);
    }

    /**
     * Get the number of total of users
     *
     * @return Long
     */
    public static Long count() {
        return UserDAO.count();
    }

    /**
     * Add new friend to an user
     *
     * @param user
     * @param friend
     *
     * @return boolean
     */
    public static boolean addFriend(Integer id, Integer friendId) {
        if (id != friendId) {
            User user = UserDAO.find(id);
            User friend = UserDAO.find(friendId);
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
    public static boolean deleteFriend(Integer id, Integer friendId) {
        if (id != friendId) {
            User user = UserDAO.find(id);
            User friend = UserDAO.find(friendId);
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
    public static boolean addFavorite(Integer id, Integer recipeId) {
        User user = UserDAO.find(id);
        Recipe recipe = RecipeDAO.find(recipeId);
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
    public static boolean deleteFavorite(Integer id, Integer recipeId) {
        User user = UserDAO.find(id);
        Recipe recipe = RecipeDAO.find(recipeId);
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
    public static boolean addRating(Integer id, Integer recipeId, double value) {
        User user = UserDAO.find(id);
        Recipe recipe = RecipeDAO.find(recipeId);
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
    public static boolean updateRating(Integer id, Integer recipeId, double value) {
        User user = UserDAO.find(id);
        Recipe recipe = RecipeDAO.find(recipeId);
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
    public static boolean deleteRating(Integer id, Integer recipeId) {
        User user = UserDAO.find(id);
        Recipe recipe = RecipeDAO.find(recipeId);
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
