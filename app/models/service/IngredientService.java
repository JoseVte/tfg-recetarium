package models.service;

import java.util.ArrayList;
import java.util.List;

import controllers.RecipeController.IngredientRequest;
import models.Recipe;
import models.Ingredient;
import models.dao.IngredientDAO;

public class IngredientService {
    private static IngredientDAO dao;

    static {
        dao = new IngredientDAO();
    }

    /**
     * Create an ingredient
     *
     * @param Ingredient data
     *
     * @return Ingredient
     */
    public static Ingredient create(Ingredient data) {
        return dao.create(data);
    }

    /**
     * Create all ingredients
     *
     * @param request
     * @param recipe
     *
     * @return List<Ingredient>
     */
    public static List<Ingredient> create(List<IngredientRequest> request, Recipe recipe) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient tmp = null;
        for (IngredientRequest aux : request) {
            tmp = new Ingredient(aux);
            tmp.recipe = recipe;
            ingredients.add(tmp);
        }
        return dao.create(ingredients);
    }
    
    /**
     * Update all ingredients
     *
     * @param request
     * @param recipe
     *
     * @return List<Ingredient>
     */
    public static List<Ingredient> update(List<IngredientRequest> request, Recipe recipe) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient tmp = null;
        for (IngredientRequest aux : request) {
            tmp = new Ingredient(aux);
            tmp.recipe = recipe;
            ingredients.add(tmp);
        }
        return dao.update(ingredients);
    }

    /**
     * Update an ingredient
     *
     * @param Ingredient data
     *
     * @return Ingredient
     */
    public static Ingredient update(Ingredient data) {
        return dao.update(data);
    }

    /**
     * Find an ingredient by id
     *
     * @param Integer id
     *
     * @return Ingredient
     */
    public static Ingredient find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete an ingredient by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Ingredient ingredient = dao.find(id);
        if (ingredient != null) {
            dao.delete(ingredient);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all ingredients
     *
     * @return List<Ingredient>
     */
    public static List<Ingredient> all() {
        return dao.all();
    }

    /**
     * Get the page of ingredients
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Ingredient>
     */
    public static List<Ingredient> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Get the number of total of ingredients
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }
}
