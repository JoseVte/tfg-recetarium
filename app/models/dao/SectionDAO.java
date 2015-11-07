package models.dao;

import java.util.List;

import models.Section;
import play.db.jpa.JPA;

public class SectionDAO {
    static String TABLE = Section.class.getName();

    /**
     * Create an section
     *
     * @param Section model
     *
     * @return Section
     */
    public static Section create(Section model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an section by id
     *
     * @param Integer id
     *
     * @return Section
     */
    public static Section find(Integer id) {
        return JPA.em().find(Section.class, id);
    }

    /**
     * Update an section
     *
     * @param Section model
     *
     * @return Section
     */
    public static Section update(Section model) {
        Section aux = JPA.em().getReference(Section.class, model.id);
        model.setCreatedAt(aux.getCreatedAt());
        return JPA.em().merge(model);
    }

    /**
     * Delete an section by id
     *
     * @param Section section
     */
    public static void delete(Section section) {
        JPA.em().remove(section);
    }

    /**
     * Get all sections
     *
     * @return List<Section>
     */
    @SuppressWarnings("unchecked")
    public static List<Section> all() {
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
    public static List<Section> paginate(Integer page, Integer size) {
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
    public static List<Section> check(String field, Object value, Integer id, String comparison) {
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
    public static List<Section> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }
}
