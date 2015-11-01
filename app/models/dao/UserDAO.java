package models.dao;

import java.util.List;
import java.util.Map;

import models.User;
import play.db.jpa.JPA;

public class UserDAO {
	static String TABLE = User.class.getName();
    /**
     * Create an user
     *
     * @param User model
     *
     * @return User
     */
    public static User create (User model) {
        model.emptyToNull();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an user by id
     *
     * @param Integer id
     *
     * @return User
     */
    public static User find(Integer id) {
        return JPA.em().find(User.class, id);
    }

    /**
     * Update an user
     *
     * @param User model
     *
     * @return User
     */
    public static User update(User model) {
        return JPA.em().merge(model);
    }

    /**
     * Delete an user by id
     *
     * @param Integer id
     */
    public static void delete(Integer id) {
        User model = JPA.em().getReference(User.class, id);
        JPA.em().remove(model);
    }

    /**
     * Get all users
     *
     * @return List<User>
     */
    @SuppressWarnings("unchecked")
	public static List<User> all() {
        return (List<User>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of users
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<User>
     */
    @SuppressWarnings("unchecked")
	public static List<User> paginate(Integer page, Integer size) {
        return (List<User>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page*size).setMaxResults(size).getResultList();
    }

    /**
     * Get the number of total row
     *
     * @return Long
     */
    public static Long count() {
        return (Long) JPA.em().createQuery("SELECT count(m) FROM " + TABLE + " m").getSingleResult();
    }
    
    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param String comparison
     *
     * @return List<User>
     */
	@SuppressWarnings("unchecked")
	public static List<User> check(String field, Object value, Integer id, String comparison) {
    	return (List<User>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " +id + " AND " + field + " " + comparison + " '" + value + "' ORDER BY id").getResultList();
    }
    
    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     *
     * @return List<User>
     */
    public static List<User> check(String field, Object value, Integer id) {
    	return check(field, value, id, "=");
    }
}