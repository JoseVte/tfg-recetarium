package dao;

import models.Ingredient;
import models.Recipe;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.*;

public class IngredientModelDAOTest extends AbstractTest {

    @Test
    public void testIngredientDAOFindIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = ingredientDAO.find(1);
                assertEquals(ingredient.name, "test");
                assertEquals(ingredient.recipe.id.intValue(), 2);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAONotFoundIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = ingredientDAO.find(0);
                assertNull(ingredient);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAOFindAllIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Ingredient> ingredient = ingredientDAO.all();
                long count = ingredientDAO.count();
                assertEquals(count, 1);

                assertEquals(ingredient.get(0).name, "test");

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAOPageIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Ingredient> ingredient = ingredientDAO.paginate(0, 1);
                assertEquals(ingredient.get(0).name, "test");
                assertEquals(ingredient.size(), 1);

                ingredient = ingredientDAO.paginate(1, 1);
                assertEquals(ingredient.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAOCreateIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient create = new Ingredient("test2", null);
                create.recipe = recipeDAO.find(1);
                Ingredient ingredient = ingredientDAO.create(create);
                assertEquals(ingredient.name, create.name);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAOCreateIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                Ingredient create = new Ingredient("test-create", null);
                Ingredient create2 = new Ingredient("test-create2", null);
                create.recipe = recipe;
                create2.recipe = recipe;
                List<Ingredient> ingredients = ingredientDAO.create(Arrays.asList(create, create2));
                assertEquals(ingredients.size(), 2);
                assertEquals(recipe.ingredients.size(), 2);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAOUpdateIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = ingredientDAO.find(1);
                ingredient.name = "Update test";
                Ingredient update = ingredientDAO.update(ingredient);
                assertEquals(update.name, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAOUpdateIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(2);
                Ingredient update = recipe.ingredients.get(0);
                update.count = "10";
                List<Ingredient> ingredients = ingredientDAO.update(Collections.singletonList(update));
                assertEquals(ingredients.size(), 1);
                assertEquals(ingredients.get(0).count, update.count);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAODeleteIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = ingredientDAO.find(1);
                long count = ingredientDAO.count();
                assertEquals(count, 1);

                ingredientDAO.delete(ingredient);

                count = ingredientDAO.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientDAODeleteNotFoundIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = ingredientDAO.find(0);

                try {
                    ingredientDAO.delete(ingredient);
                } catch (Exception e) {
                }

                successTest();
            });
        });
    }
}
