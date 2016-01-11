package models.dao;

import java.util.ArrayList;
import java.util.List;

import models.Ingredient;
import models.base.CrudDAO;

public class IngredientDAO extends CrudDAO<Ingredient> {
    public IngredientDAO() {
        super(Ingredient.class);
    }

    /**
     * Create all ingredients
     *
     * @param ingredientsRequest
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
     * @param ingredientsRequest
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
