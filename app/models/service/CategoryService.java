package models.service;

import java.util.List;

import models.Category;
import models.dao.CategoryDAO;

public class CategoryService {
    private static CategoryDAO dao;

    static {
        dao = new CategoryDAO();
    }

    /**
     * Create a section
     *
     * @param Category data
     *
     * @return Section
     */
    public static Category create(Category data) {
        return dao.create(data);
    }

    /**
     * Update a section
     *
     * @param Category data
     *
     * @return Section
     */
    public static Category update(Category data) {
        return dao.update(data);
    }

    /**
     * Find a section by id
     *
     * @param Integer id
     *
     * @return Section
     */
    public static Category find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete a section by id
     *
     * @param Integer id
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
     * @param Integer page
     * @param Integer size
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
