package models.service;

import models.File;
import models.User;
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
     * Find a files by id
     *
     * @param user User
     * @param id   Integer
     *
     * @return File
     */
    public static File find(User user, Integer id) {
        return dao.find(user, id);
    }

    /**
     * Check if the file for the user exists
     *
     * @param user User
     * @param id   Integer
     *
     * @return File
     */
    public static boolean checkOwner(User user, Integer id) {
        return dao.find(user, id) != null;
    }

    /**
     * Delete a files if it can
     *
     * @param file File
     * @param user User
     */
    public static Boolean delete(File file, User user) {
        if (file != null && user != null && dao.canDelete(file, user)) {
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
     * Get all files of one user
     *
     * @param idUser Integer
     *
     * @return List<File>
     */
    public static List<File> all(Integer idUser) {
        return dao.all(idUser);
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
