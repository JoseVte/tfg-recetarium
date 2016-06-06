package models.dao;

import models.Category;
import models.base.CrudDAO;
import models.service.CategoryService;
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

    /**
     * Get the page of models order by field
     *
     * @param search String
     * @param page   Integer
     * @param size   Integer
     * @param order  String
     *
     * @return List<Category>
     */
    public List<Category> paginate(Integer page, Integer size, String search, String order) {
        return JPA.em().createQuery("SELECT categories FROM " + TABLE + " categories WHERE " + Category.Search(search) + " ORDER BY " + order, Category.class).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    /**
     * Count the all categories with search parameter
     *
     * @param search String
     *
     * @return Long
     */
    public Long count(String search) {
        return JPA.em().createQuery("SELECT count(categories) FROM " + TABLE + " categories WHERE " + Category.Search(search), Long.class).getSingleResult();
    }

    public Integer delete(List<Integer> ids) {
        int deleted = 0;
        for (Integer id : ids) {
            if (CategoryService.delete(id)){
                deleted++;
            }
        }
        return deleted;
    }
}
