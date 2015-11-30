package models.dao;

import java.util.List;

import models.Category;
import models.base.CrudDAO;
import play.db.jpa.JPA;

public class CategoryDAO extends CrudDAO<Category> {
    public CategoryDAO() {
        super(Category.class);
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     * @param String comparison
     *
     * @return List<Category>
     */
    public List<Category> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id", Category.class).getResultList();
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     *
     * @return List<Category>
     */
    public List<Category> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }
}
