package models.dao;

import models.File;
import models.Recipe;
import models.User;
import models.base.CrudDAO;
import models.base.Model;
import models.manytomany.RecipeFiles;
import models.service.UserService;
import play.db.jpa.JPA;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

public class FileDAO extends CrudDAO<File> {
    public FileDAO() {
        super(File.class);
    }

    /**
     * Get all files of one user
     *
     * @param idUser Integer
     *
     * @return List<File>
     */
    public List<File> all(Integer idUser) {
        return JPA.em().createQuery("SELECT files FROM " + TABLE + " files WHERE files.user = '" + idUser + "'", File.class).getResultList();
    }

    /**
     * Find a file with user id
     *
     * @param idUser Integer
     * @param idFile Integer
     *
     * @return File
     */
    public File find(Integer idUser, Integer idFile) {
        try {
            return JPA.em().createQuery("SELECT files FROM " + TABLE + " files WHERE files.id = " + idFile + " AND files.user = '" + idUser + "'", File.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find a file with user id
     *
     * @param idUser   Integer
     * @param filename String
     *
     * @return File
     */
    public File find(Integer idUser, String filename) {
        try {
            return JPA.em().createQuery("SELECT files FROM " + TABLE + " files WHERE files.newTitle = '" + filename + "' AND files.user = '" + idUser + "'", File.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find a file with user id
     *
     * @param user   User
     * @param idFile Integer
     *
     * @return File
     */
    public File find(User user, Integer idFile) {
        try {
            if (user != null) {
                if (user.isAdmin()) return this.find(idFile);
                return JPA.em().createQuery("SELECT files FROM " + TABLE + " files WHERE files.id = '" + idFile + "' AND files.user = " + user.id, File.class).getSingleResult();
            }
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find all files for an user
     *
     * @param email String
     *
     * @return List<File>
     */
    public List<File> findByOwner(String email) {
        User logged = UserService.findByEmailAddress(email);
        if (logged != null) {
            return JPA.em().createQuery("SELECT files FROM " + TABLE + " files WHERE files.user = '" + logged.id + "'", File.class).getResultList();
        }
        return new ArrayList<File>();
    }

    /**
     * Find a files for an user
     *
     * @param email  String
     * @param idFile IntegerRecipe
     *
     * @return File
     */
    public File findByOwner(String email, Integer idFile) {
        try {
            User logged = UserService.findByEmailAddress(email);
            if (logged != null) {
                if (logged.isAdmin()) {
                    return find(idFile);
                }
                return JPA.em().createQuery("SELECT files FROM " + TABLE + " files WHERE files.id = '" + idFile + "' AND files.user = '" + logged.id + "'", File.class).getSingleResult();
            }
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean canDelete(File file, User user) {
        String query = "SELECT files FROM " + Recipe.class.getName() + " files WHERE image_main_id = " + file.id;
        if (user.isAdmin()) {
            return JPA.em().createQuery(query).getResultList().size() == 0;
        }
        return JPA.em().createQuery(query + " AND user = " + user.id).getResultList().size() == 0;
    }

    /**
     * Delete a file by id
     *
     * @param model Model
     */
    @Override
    public void delete(Model file) {
        JPA.em().createQuery("DELETE FROM " + RecipeFiles.class.getName() + " WHERE file_id = " + file.id).executeUpdate();
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(file);
        JPA.em().remove(file);
    }
}
