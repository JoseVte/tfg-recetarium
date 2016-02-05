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
     * Create a section
     *
     * @param data Category
     *
     * @return Section
     */
    public static Category create(Category data) {
        return dao.create(data);
    }

    /**
     * Update a section
     *
     * @param data Category
     *
     * @return Section
     */
    public static Category update(Category data) {
        return dao.update(data);
    }

    /**
     * Find a section by id
     *
     * @param id Integer
     *
     * @return Section
     */
    public static Category find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete a section by id
     *
     * @param id Integer
     */
    public static Boolean delete(Integer id) {
        Category section = dao.find(id);
        if (section != null) {
            dao.delete(section);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all sections
     *
     * @return List<Section>
     */
    public static List<Category> all() {
        return dao.all();
    }

    /**
     * Get the page of sections
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<Section>
     */
    public static List<Category> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Get the number of total of sections
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }
}
