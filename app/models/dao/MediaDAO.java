package models.dao;

import java.util.List;

import models.Media;
import play.db.jpa.JPA;

public class MediaDAO {
	static String TABLE = Media.class.getName();
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
    	return (List<Media>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " +id + " AND (filename = '" + filename + "' AND recipe_id = '" + recipe_id + "') ORDER BY id").getResultList();
    }
}
