package models.dao;

import models.Ingredient;
import models.base.CrudDAO;

import java.util.ArrayList;
import java.util.List;

public class IngredientDAO extends CrudDAO<Ingredient> {
    public IngredientDAO() {
        super(Ingredient.class);
    }

    /**
     * Create all ingredients
     *
     * @param ingredientsRequest List<Ingredient>
     *
     * @return List<Ingredient>
     */
    public List<Ingredient> create(List<Ingredient> ingredientsRequest) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (Ingredient ingredient : ingredientsRequest) {
            create(ingredient);
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    /**
     * Update all ingredients
     *
     * @param ingredientsRequest List<Ingredient>
     *
     * @return List<Ingredient>
     */
    public List<Ingredient> update(List<Ingredient> ingredientsRequest) {
        List<Ingredient> ingredients = new ArrayList<Ingredient>();
        for (Ingredient ingredient : ingredientsRequest) {
            update(ingredient);
            ingredients.add(ingredient);
        }
        return ingredients;
    }
}
