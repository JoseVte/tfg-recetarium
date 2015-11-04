package models.service;

import java.util.List;

import models.Recipe;
import models.dao.RecipeDAO;

public class RecipeService {
    /**
     * Create an recipe
     *
     * @param Recipe data
     *
     * @return Recipe
     */
    public static Recipe create(Recipe data) {
        return RecipeDAO.create(data);
    }

    /**
     * Update an recipe
     *
     * @param Recipe data
     *
     * @return Recipe
     */
    public static Recipe update(Recipe data) {
        return RecipeDAO.update(data);
    }

    /**
     * Find an recipe by id
     *
     * @param Integer id
     *
     * @return Recipe
     */
    public static Recipe find(Integer id) {
        return RecipeDAO.find(id);
    }

    /**
     * Delete an recipe by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Recipe recipe = RecipeDAO.find(id);
        if (recipe != null) {
            RecipeDAO.delete(id);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all recipes
     *
     * @return List<Recipe>
     */
    public static List<Recipe> all() {
        return RecipeDAO.all();
    }

    /**
     * Get the page of recipes
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Recipe>
     */
    public static List<Recipe> paginate(Integer page, Integer size) {
        return RecipeDAO.paginate(page, size);
    }

    /**
     * Get the number of total of recipes
     *
     * @return Long
     */
    public static Long count() {
        return RecipeDAO.count();
    }
}
