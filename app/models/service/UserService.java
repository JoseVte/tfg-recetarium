package models.service;

import java.util.List;

import models.User;
import models.dao.UserDAO;

public class UserService {
    /**
     * Create an user
     *
     * @param User data
     *
     * @return User
     */
    public static User create(User data) {
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
}
