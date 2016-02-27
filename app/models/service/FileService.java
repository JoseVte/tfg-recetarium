package models.service;

import models.File;
import models.dao.FileDAO;

import java.util.List;

public class FileService {
    public static char FILE_SEPARARTOR = java.io.File.separatorChar;
    private static FileDAO dao;

    static {
        dao = new FileDAO();
    }

    /**
     * Create a files
     *
     * @param data File
     *
     * @return File
     */
    public static File create(File data) {
        return dao.create(data);
    }

    /**
     * Update a files
     *
     * @param data File
     *
     * @return File
     */
    public static File update(File data) {
        return dao.update(data);
    }

    /**
     * Find a files by id
     *
     * @param id Integer
     *
     * @return File
     */
    public static File find(Integer id) {
        return dao.find(id);
    }

    /**
     * Find a files by id
     *
     * @param idUser   Integer
     * @param filename String
     *
     * @return File
     */
    public static File find(Integer idUser, String filename) {
        return dao.find(idUser, filename);
    }

    /**
     * Find a files by id
     *
     * @param idUser Integer
     * @param id     Integer
     *
     * @return File
     */
    public static File find(Integer idUser, Integer id) {
        return dao.find(idUser, id);
    }

    /**
     * Delete a files by id
     *
     * @param id    Integer
     * @param email String
     */
    public static Boolean delete(Integer id, String email) {
        File file = dao.findByOwner(email, id);
        if (file != null) {
            dao.delete(file);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all files
     *
     * @return List<File>
     */
    public static List<File> all() {
        return dao.all();
    }

    /**
     * Get the page of files
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<File>
     */
    public static List<File> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Get the number of total of files
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }
}
