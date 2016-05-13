package models.dao;

import models.Category;
import models.base.CrudDAO;
import play.db.jpa.JPA;

import java.util.List;

public class CategoryDAO extends CrudDAO<Category> {
    public CategoryDAO() {
        super(Category.class);
    }

    /**
     * Where clause
     *
     * @param field      String
     * @param value      Object
     * @param id         Integer
     * @param comparison String
     *
     * @return List<Category>
     */
    private List<Category> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT categories FROM " + TABLE + " categories WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id", Category.class).getResultList();
    }

    /**
     * Where clause
     *
     * @param field String
     * @param value Object
     * @param id    Integer
     *
     * @return List<Category>
     */
    public List<Category> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }
}
