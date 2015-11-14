package models.dao;

import java.util.List;

import models.Category;
import play.db.jpa.JPA;

public class CategoryDAO {
    static String TABLE = Category.class.getName();

    /**
     * Create a section
     *
     * @param Category model
     *
     * @return Section
     */
    public static Category create(Category model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find a section by id
     *
     * @param Integer id
     *
     * @return Section
     */
    public static Category find(Integer id) {
        return JPA.em().find(Category.class, id);
    }

    /**
     * Update a section
     *
     * @param Category model
     *
     * @return Section
     */
    public static Category update(Category model) {
        Category aux = JPA.em().getReference(Category.class, model.id);
        model.setCreatedAt(aux.getCreatedAt());
        return JPA.em().merge(model);
    }

    /**
     * Delete a section by id
     *
     * @param Category section
     */
    public static void delete(Category section) {
        JPA.em().remove(section);
    }

    /**
     * Get all sections
     *
     * @return List<Section>
     */
    @SuppressWarnings("unchecked")
    public static List<Category> all() {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of sections
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Section>
     */
    @SuppressWarnings("unchecked")
    public static List<Category> paginate(Integer page, Integer size) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page * size)
                .setMaxResults(size).getResultList();
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
     * @return List<Recipe>
     */
    @SuppressWarnings("unchecked")
    public static List<Category> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id").getResultList();
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     *
     * @return List<Recipe>
     */
    public static List<Category> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }
}
