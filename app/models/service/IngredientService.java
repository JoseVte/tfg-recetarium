package models.service;

import controllers.RecipeController.IngredientRequest;
import models.Ingredient;
import models.Recipe;
import models.dao.IngredientDAO;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class IngredientService {
    private static final IngredientDAO dao;

    static {
        dao = new IngredientDAO();
    }

    /**
     * Create an ingredient
     *
     * @param data Ingredient
     *
     * @return Ingredient
     */
    public static Ingredient create(Ingredient data) {
        return dao.create(data);
    }

    /**
     * Create all ingredients
     *
     * @param request List<IngredientRequest>
     * @param recipe  Recipe
     *
     * @return List<Ingredient>
     */
    public static List<Ingredient> create(List<IngredientRequest> request, Recipe recipe) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient tmp;
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
     * @param request List<IngredientRequest>
     * @param recipe  Recipe
     *
     * @return List<Ingredient>
     */
    public static List<Ingredient> update(List<IngredientRequest> request, Recipe recipe) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        Ingredient tmp;
        for (IngredientRequest aux : request) {
            tmp = new Ingredient(aux);
            tmp.recipe = recipe;
            ingredients.add(tmp);
        }
        ingredients = dao.update(ingredients);
        final List<Ingredient> finalIngredients = ingredients;
        recipe.ingredients.replaceAll(ingredient -> {
            if (finalIngredients.contains(ingredient)) {
                return finalIngredients.get(finalIngredients.indexOf(ingredient));
            }
            return ingredient;
        });
        return ingredients;
    }

    /**
     * Update an ingredient
     *
     * @param data Ingredient
     *
     * @return Ingredient
     */
    public static Ingredient update(Ingredient data) {
        return dao.update(data);
    }

    /**
     * Find an ingredient by id
     *
     * @param id Integer
     *
     * @return Ingredient
     */
    public static Ingredient find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete an ingredient by id
     *
     * @param id Integer
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
     * @param page Integer
     * @param size Integer
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
