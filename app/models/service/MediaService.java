package models.service;

import java.util.List;

import models.Media;
import models.dao.MediaDAO;

public class MediaService {
    /**
     * Create a media
     *
     * @param Media data
     *
     * @return Media
     */
    public static Media create(Media data) {
        return MediaDAO.create(data);
    }

    /**
     * Update a media
     *
     * @param Media data
     *
     * @return Media
     */
    public static Media update(Media data) {
        return MediaDAO.update(data);
    }

    /**
     * Find a media by id
     *
     * @param Integer id
     *
     * @return Media
     */
    public static Media find(Integer id) {
        return MediaDAO.find(id);
    }

    /**
     * Delete a media by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Media media = MediaDAO.find(id);
        if (media != null) {
            MediaDAO.delete(media);
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
        return MediaDAO.all();
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
        return MediaDAO.paginate(page, size);
    }

    /**
     * Get the number of total of media
     *
     * @return Long
     */
    public static Long count() {
        return MediaDAO.count();
    }
}
