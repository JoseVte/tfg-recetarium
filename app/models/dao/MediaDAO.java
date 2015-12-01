package models.dao;

import java.util.List;

import models.Media;
import models.base.CrudDAO;
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
}
