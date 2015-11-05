package models.dao;

import java.util.List;

import models.Tag;
import play.db.jpa.JPA;

public class TagDAO {
    static String TABLE = Tag.class.getName();

    /**
     * Create an tag
     *
     * @param Tag model
     *
     * @return Tag
     */
    public static Tag create(Tag model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an tag by id
     *
     * @param Integer id
     *
     * @return Tag
     */
    public static Tag find(Integer id) {
        return JPA.em().find(Tag.class, id);
    }

    /**
     * Update an tag
     *
     * @param Tag model
     *
     * @return Tag
     */
    public static Tag update(Tag model) {
        Tag aux = JPA.em().getReference(Tag.class, model.id);
        model.setCreatedAt(aux.getCreatedAt());
        return JPA.em().merge(model);
    }

    /**
     * Delete an tag by id
     *
     * @param Tag tag
     */
    public static void delete(Tag tag) {
        JPA.em().remove(tag);
    }

    /**
     * Get all tags
     *
     * @return List<Tag>
     */
    @SuppressWarnings("unchecked")
    public static List<Tag> all() {
        return (List<Tag>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of tags
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Tag>
     */
    @SuppressWarnings("unchecked")
    public static List<Tag> paginate(Integer page, Integer size) {
        return (List<Tag>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page * size)
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
     * @param Integer recipe_id
     * @param String filename
     * @param Integer id
     *
     * @return List<Tag>
     */
    @SuppressWarnings("unchecked")
    public static List<Tag> check(Integer recipe_id, String filename, Integer id) {
        return (List<Tag>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND (filename = '"
                + filename + "' AND recipe_id = '" + recipe_id + "') ORDER BY id").getResultList();
    }
}
