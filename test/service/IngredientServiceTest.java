package service;

import controllers.RecipeController.IngredientRequest;
import models.Ingredient;
import models.service.IngredientService;
import models.service.RecipeService;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class IngredientServiceTest extends AbstractTest {

    @Test
    public void testIngredientServiceFindIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = IngredientService.find(1);
                assertEquals(ingredient.name, "test");
                assertEquals(ingredient.recipe.id.intValue(), 2);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceNotFoundIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = IngredientService.find(0);
                assertNull(ingredient);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceFindAllIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Ingredient> ingredient = IngredientService.all();
                long count = IngredientService.count();
                assertEquals(count, 1);

                assertEquals(ingredient.get(0).name, "test");

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServicePageIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Ingredient> ingredient = IngredientService.paginate(0, 1);
                assertEquals(ingredient.get(0).name, "test");
                assertEquals(ingredient.size(), 1);

                ingredient = IngredientService.paginate(1, 1);
                assertEquals(ingredient.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceCreateIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient create = new Ingredient("test2", null);
                create.recipe = RecipeService.find(1);
                Ingredient ingredient = IngredientService.create(create);
                assertEquals(ingredient, create);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceCreateIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                IngredientRequest create = new IngredientRequest();
                create.name = "test";
                List<Ingredient> ingredients = IngredientService.create(Collections.singletonList(create), RecipeService.find(1));
                assertEquals(ingredients.size(), 1);
                assertEquals(RecipeService.find(1).ingredients.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceUpdateIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Ingredient ingredient = IngredientService.find(1);
                ingredient.name = "Update test";
                Ingredient update = IngredientService.update(ingredient);
                assertEquals(update.name, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceUpdateIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                IngredientRequest update = new IngredientRequest();
                update.id = 1;
                update.name = "test";
                update.count = "test update";
                List<Ingredient> ingredients = IngredientService.update(Collections.singletonList(update), RecipeService.find(1));
                assertEquals(ingredients.size(), 1);
                assertEquals(ingredients.get(0).count, "test update");

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceDeleteIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = IngredientService.count();
                assertEquals(count, 1);

                assertTrue(IngredientService.delete(1));

                count = IngredientService.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testIngredientServiceDeleteNotFoundIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(IngredientService.delete(0));

                successTest();
            });
        });
    }
}
