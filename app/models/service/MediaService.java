package models.service;

import models.Media;
import models.dao.MediaDAO;

import java.io.File;
import java.util.List;

public class MediaService {
    public static char FILE_SEPARARTOR = File.separatorChar;
    private static MediaDAO dao;

    static {
        dao = new MediaDAO();
    }

    /**
     * Create a media
     *
     * @param data Media
     *
     * @return Media
     */
    public static Media create(Media data) {
        return dao.create(data);
    }

    /**
     * Update a media
     *
     * @param data Media
     *
     * @return Media
     */
    public static Media update(Media data) {
        return dao.update(data);
    }

    /**
     * Find a media by id
     *
     * @param id Integer
     *
     * @return Media
     */
    public static Media find(Integer id) {
        return dao.find(id);
    }

    /**
     * Find a media by id
     *
     * @param idRecipe Integer
     * @param filename String
     *
     * @return Media
     */
    public static Media find(Integer idRecipe, String filename) {
        List<Media> list = dao.check(idRecipe, filename, null);
        return (list.isEmpty() ? null : list.get(0));
    }

    /**
     * Find a media by id
     *
     * @param idRecipe Integer
     * @param id       Integer
     *
     * @return Media
     */
    public static Media find(Integer idRecipe, Integer id) {
        return dao.check(idRecipe, id);
    }

    /**
     * Delete a media by id
     *
     * @param id    Integer
     * @param email String
     */
    public static Boolean delete(Integer id, String email) {
        Media media = dao.findByOwner(email, id);
        if (media != null) {
            dao.delete(media);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all media
     *
     * @return List<Media>
     */
    public static List<Media> all() {
        return dao.all();
    }

    /**
     * Get the page of media
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<Media>
     */
    public static List<Media> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Get the number of total of media
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }
}
