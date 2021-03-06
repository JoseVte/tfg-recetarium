package service;

import controllers.RecipeController.IngredientRequest;
import models.Ingredient;
import models.Recipe;
import models.enums.RecipeDifficulty;
import models.enums.RecipeVisibility;
import models.service.FileService;
import models.service.RecipeService;
import models.service.UserService;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class RecipeServiceTest extends AbstractTest {

    @Test
    public void testRecipeServiceFindRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.find(1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.slug, "test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.files.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.find(0);
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceFindBySlugRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.findBySlug("test");
                assertEquals(recipe.id.intValue(), 1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.files.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceFindBySlugAndIdRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.findBySlugAndId("test", 2);
                assertEquals(recipe.id.intValue(), 1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.files.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                recipe = RecipeService.findBySlugAndId("test-2", 2);
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceNotFoundBySlugRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.findBySlug("not-found");
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceFindAllRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Recipe> recipes = RecipeService.all();
                long count = RecipeService.count("", null);
                assertEquals(count, 2);

                assertEquals(recipes.get(0).title, "Test");

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServicePageRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Recipe> recipes = RecipeService.paginate(0, 1, "", null);
                assertEquals(recipes.get(0).title, "Test");
                assertEquals(recipes.size(), 1);

                recipes = RecipeService.paginate(1, 1, "", null);
                assertEquals(recipes.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceSearchRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Recipe> recipes = RecipeService.paginate(0, 10, "", null);
                assertEquals(recipes.get(0).title, "Test");
                assertEquals(recipes.size(), 2);

                recipes = RecipeService.paginate(0, 10, "Test 2", null);
                assertEquals(recipes.size(), 1);
                long count = RecipeService.count("Test 2", null);
                assertEquals(count, 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceCreateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe create = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                Recipe recipe = RecipeService.create(create);
                assertEquals(recipe, create);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.find(1);
                recipe.title = "Update test";
                Recipe update = RecipeService.update(recipe);
                assertEquals(update.title, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = RecipeService.count("", null);
                assertEquals(count, 2);

                assertTrue(RecipeService.delete(1, "test@testing.dev"));

                count = RecipeService.count("", null);
                assertEquals(count, 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteRecipeAsAdmin() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = RecipeService.count("", null);
                assertEquals(count, 2);

                assertTrue(RecipeService.delete(1, "admin@admin.dev"));

                count = RecipeService.count("", null);
                assertEquals(count, 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(RecipeService.delete(0, "test@testing.dev"));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteNotOwnerRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(RecipeService.delete(1, ""));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceCheckOwner() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertTrue(RecipeService.checkOwner("test@testing.dev", 1));
                assertTrue(RecipeService.checkOwner("admin@admin.dev", 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceCheckOwnerFail() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(RecipeService.checkOwner("test@testing.dev", 0));
                assertFalse(RecipeService.checkOwner("", 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                recipe = RecipeService.create(recipe);

                assertEquals(recipe.tags.size(), 0);

                List<Integer> tags = Collections.singletonList(1);
                RecipeService.addTags(tags, recipe.id);

                assertEquals(recipe.tags.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addTag(1, recipe.id));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addTag(1, 0));
                assertFalse(RecipeService.addTag(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddTagAlreadyTagged() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addTag(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeService.find(1);
                assertEquals(recipe.tags.size(), 1);
                assertTrue(RecipeService.deleteTags(recipe.id));
                assertEquals(recipe.tags.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(RecipeService.deleteTag(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.deleteTag(1, 0));
                assertFalse(RecipeService.deleteTag(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteTagNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                recipe = RecipeService.create(recipe);

                assertFalse(RecipeService.deleteTag(1, recipe.id));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addFavorite(1, recipe.id));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addFavorite(1, 0));
                assertFalse(RecipeService.addFavorite(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddFavoriteAlreadyFav() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addFavorite(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(RecipeService.deleteFavorite(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.deleteFavorite(1, 0));
                assertFalse(RecipeService.deleteFavorite(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteFavoriteNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.deleteFavorite(2, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addRating(1, recipe.id, 4.3));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addRating(1, 0, 0.0));
                assertFalse(RecipeService.addRating(0, 1, 0.0));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddRatingAlreadyRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addRating(1, 1, 0.0));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addRating(1, 1, -0.01));
                assertFalse(RecipeService.addRating(1, 1, 5.01));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(RecipeService.updateRating(1, 1, 0.0));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.updateRating(1, 0, 0.0));
                assertFalse(RecipeService.updateRating(0, 1, 0.0));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.updateRating(1, 1, -0.01));
                assertFalse(RecipeService.updateRating(1, 1, 5.01));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(RecipeService.deleteRating(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.deleteRating(1, 0));
                assertFalse(RecipeService.deleteRating(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), 0, RecipeDifficulty.EASY, RecipeVisibility.PUBLIC, UserService.find(1), null, FileService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addCategory(1, recipe.id));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddCategoryNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addCategory(1, 0));
                assertFalse(RecipeService.addCategory(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddCategoryAlreadyCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.addCategory(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(RecipeService.updateCategory(1, 1));
                assertTrue(RecipeService.updateCategory(2, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateCategoryNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.updateCategory(1, 0));
                assertFalse(RecipeService.updateCategory(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(RecipeService.deleteCategory(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteCategoryNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(RecipeService.deleteCategory(1, 0));
                assertFalse(RecipeService.deleteCategory(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceAddIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                IngredientRequest ingredient = new IngredientRequest();
                ingredient.name = "test";
                Ingredient model = RecipeService.addIngredient(1, ingredient);
                assertNotNull(model);
                assertEquals(model.name, ingredient.name);
                assertEquals(model.recipe.id.intValue(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceUpdateIngredients() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                Recipe recipe = RecipeService.find(2);
                assertEquals(recipe.ingredients.size(), 1);
                recipe.ingredients.clear();
                RecipeService.updateIngredients(recipe);
                assertEquals(recipe.ingredients.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeServiceDeleteIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                Recipe recipe = RecipeService.find(2);
                assertEquals(recipe.ingredients.size(), 1);
                assertTrue(RecipeService.deleteIngredient(recipe.id, recipe.ingredients.get(0).id));
                assertFalse(RecipeService.deleteIngredient(recipe.id, 1));

                successTest();
            });
        });
    }
}
