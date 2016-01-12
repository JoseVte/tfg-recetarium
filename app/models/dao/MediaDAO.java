package models.dao;

import java.util.List;

import javax.persistence.NoResultException;

import models.Media;
import models.User;
import models.base.CrudDAO;
import models.service.UserService;
import play.db.jpa.JPA;

public class MediaDAO extends CrudDAO<Media> {
    public MediaDAO() {
        super(Media.class);
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
    public List<Media> check(Integer recipe_id, String filename, Integer id) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND (filename = '" + filename
                + "' AND recipe_id = '" + recipe_id + "') ORDER BY id", Media.class).getResultList();
    }
    
    /**
     * Find a recipe if the email is from the user creator or an admin
     *
     * @param String email
     * @param Integer idRecipe
     *
     * @return Recipe
     */
    public Media findByOwner(String email, Integer idMedia) {
        try {
            User logged = UserService.findByEmailAddress(email);
            if (logged != null) {
                if (logged.isAdmin()) {
                    return find(idMedia);
                }
                return JPA.em().createQuery("SELECT m FROM " + TABLE + " m JOIN m.recipe r JOIN r.user u WHERE m.id = '" + idMedia
                        + "' AND u.id = '" + logged.id + "'", Media.class).getSingleResult();
            }
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }
}
