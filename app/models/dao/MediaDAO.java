package models.dao;

import java.util.List;

import models.Media;
import play.db.jpa.JPA;

public class MediaDAO {
    static String TABLE = Media.class.getName();

    /**
     * Create an media
     *
     * @param Media model
     *
     * @return Media
     */
    public static Media create(Media model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an media by id
     *
     * @param Integer id
     *
     * @return Media
     */
    public static Media find(Integer id) {
        return JPA.em().find(Media.class, id);
    }

    /**
     * Update an media
     *
     * @param Media model
     *
     * @return Media
     */
    public static Media update(Media model) {
        Media aux = JPA.em().getReference(Media.class, model.id);
        model.setCreatedAt(aux.getCreatedAt());
        return JPA.em().merge(model);
    }

    /**
     * Delete an media by id
     *
     * @param Media media
     */
    public static void delete(Media media) {
        JPA.em().remove(media);
    }

    /**
     * Get all medias
     *
     * @return List<Media>
     */
    @SuppressWarnings("unchecked")
    public static List<Media> all() {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of medias
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Media>
     */
    @SuppressWarnings("unchecked")
    public static List<Media> paginate(Integer page, Integer size) {
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
     * @param Integer recipe_id
     * @param String filename
     * @param Integer id
     *
     * @return List<Media>
     */
    @SuppressWarnings("unchecked")
    public static List<Media> check(Integer recipe_id, String filename, Integer id) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND (filename = '" + filename
                + "' AND recipe_id = '" + recipe_id + "') ORDER BY id").getResultList();
    }
}
