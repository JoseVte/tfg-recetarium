package models.dao;

import java.util.List;

import models.manytomany.Rating;
import play.db.jpa.JPA;

public class RatingDAO {
    static String TABLE = Rating.class.getName();

    /**
     * Create an rating
     *
     * @param Rating model
     *
     * @return Rating
     */
    public static Rating create(Rating model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an rating by id
     *
     * @param Integer id
     *
     * @return Rating
     */
    public static Rating find(Integer id) {
        return JPA.em().find(Rating.class, id);
    }

    /**
     * Update an rating
     *
     * @param Rating model
     *
     * @return Rating
     */
    public static Rating update(Rating model) {
        return JPA.em().merge(model);
    }

    /**
     * Delete an rating by id
     *
     * @param Rating rating
     */
    public static void delete(Rating rating) {
        JPA.em().remove(rating);
    }

    /**
     * Get all ratings
     *
     * @return List<Rating>
     */
    @SuppressWarnings("unchecked")
    public static List<Rating> all() {
        return (List<Rating>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of ratings
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Rating>
     */
    @SuppressWarnings("unchecked")
    public static List<Rating> paginate(Integer page, Integer size) {
        return (List<Rating>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id")
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
     * @param Integer recipe_id
     * @param String filename
     * @param Integer id
     *
     * @return List<Rating>
     */
    @SuppressWarnings("unchecked")
    public static List<Rating> check(Integer recipe_id, String filename, Integer id) {
        return (List<Rating>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id
                + " AND (filename = '" + filename + "' AND recipe_id = '" + recipe_id + "') ORDER BY id")
                .getResultList();
    }
}
