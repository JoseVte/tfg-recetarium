package models.service;

import models.Category;
import models.dao.CategoryDAO;

import java.util.List;

public class CategoryService {
    private static final CategoryDAO dao;

    static {
        dao = new CategoryDAO();
    }

    /**
     * Create a category
     *
     * @param data Category
     *
     * @return Category
     */
    public static Category create(Category data) {
        return dao.create(data);
    }

    /**
     * Update a category
     *
     * @param data Category
     *
     * @return Category
     */
    public static Category update(Category data) {
        return dao.update(data);
    }

    /**
     * Find a category by id
     *
     * @param id Integer
     *
     * @return Category
     */
    public static Category find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete a category by id
     *
     * @param id Integer
     */
    public static Boolean delete(Integer id) {
        Category category = dao.find(id);
        if (category != null) {
            dao.delete(category);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Delete categories by id
     *
     * @param ids List<Integer>
     */
    public static Integer delete(List<Integer> ids) {
        return dao.delete(ids);
    }

    /**
     * Get all categories
     *
     * @return List<Category>
     */
    public static List<Category> all() {
        return dao.all();
    }

    /**
     * Get the page of categories
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<Category>
     */
    public static List<Category> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Get the page of categories
     *
     * @param page Integer
     * @param size Integer
     * @param search String
     * @param order String
     *
     * @return List<Category>
     */
    public static List<Category> paginate(Integer page, Integer size, String search, String order) {
        return dao.paginate(page, size, search, order);
    }

    /**
     * Get the number of total of categories
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }

    /**
     * Get the number of total of categories
     *
     * @param search String
     *
     * @return Long
     */
    public static Long count(String search) {
        return dao.count(search);
    }
}
