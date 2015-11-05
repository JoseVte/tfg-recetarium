package models.dao;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import models.User;
import play.db.jpa.JPA;
import util.Encryptation;

public class UserDAO {
    static String TABLE = User.class.getName();

    /**
     * Create an user
     *
     * @param User model
     *
     * @return User
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    public static User create(User model) throws NoSuchAlgorithmException, InvalidKeySpecException {
        model.prePersistData();
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
        User aux = JPA.em().getReference(User.class, model.id);
        model.setCreatedAt(aux.getCreatedAt());
        return JPA.em().merge(model);
    }

    /**
     * Delete an user by id
     *
     * @param User user
     */
    public static void delete(User user) {
        JPA.em().remove(user);
    }

    /**
     * Get all users
     *
     * @return List<User>
     */
    @SuppressWarnings("unchecked")
    public static List<User> all() {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
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
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id")
                .setFirstResult(page * size).setMaxResults(size).getResultList();
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
     * @param Integer id
     * @param String comparison
     *
     * @return List<User>
     */
    @SuppressWarnings("unchecked")
    public static List<User> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field
                + " " + comparison + " '" + value + "' ORDER BY id").getResultList();
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
    public static List<User> check(String field, Object value, Integer id) {
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
}
