package models.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import models.Media;
import models.dao.MediaDAO;

public class MediaService {
    private static MediaDAO dao;
    public static char FILE_SEPARARTOR = File.separatorChar;

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
     * Delete a media by id
     *
     * @param Integer id
     * @param String email
     */
    public static Boolean delete(Integer id, String email) {
        Media media = dao.findByOwner(email, id);
        if (media != null) {
            try {
                String pathDir = "public" + FILE_SEPARARTOR + "files" + FILE_SEPARARTOR + media.recipe.id;
                Path path = Paths.get(pathDir + media.filename);
                Files.delete(path);
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return false;
            }
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
    
    /**
     * Where clause
     *
     * @param Integer recipe_id
     * @param String filename
     * @param Integer id
     *
     * @return List<Media>
     */
    public static List<Media> check(Integer recipe_id, String filename, Integer id) {
        return dao.check(recipe_id, filename, id);
    }
}
