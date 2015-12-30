package models.service;

import java.io.File;
import java.util.List;

import models.Media;
import models.dao.MediaDAO;

public class MediaService {
    private static MediaDAO dao;
    public static char      FILE_SEPARARTOR = File.separatorChar;

    static {
        dao = new MediaDAO();
    }

    /**
     * Create a media
     *
     * @param Media data
     *
     * @return Media
     */
    public static Media create(Media data) {
        return dao.create(data);
    }

    /**
     * Update a media
     *
     * @param Media data
     *
     * @return Media
     */
    public static Media update(Media data) {
        return dao.update(data);
    }

    /**
     * Find a media by id
     *
     * @param Integer id
     *
     * @return Media
     */
    public static Media find(Integer id) {
        return dao.find(id);
    }

    /**
     * Find a media by id
     * 
     * @param Integer
     *            id
     * @return Media
     */
    public static Media find(Integer idRecipe, String filename) {
        List<Media> list = dao.check(idRecipe, filename, null);
        return (list.isEmpty() ? null : list.get(0));
    }

    /**
     * Delete a media by id
     *
     * @param Integer id
     * @param String email
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
     * @param Integer page
     * @param Integer size
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
