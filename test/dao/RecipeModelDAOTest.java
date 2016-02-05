package dao;

import models.*;
import models.dao.RecipeDAO;
import models.enums.RecipeDifficulty;
import models.enums.RecipeVisibility;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.*;

public class RecipeModelDAOTest extends AbstractTest {

    @Test
    public void testRecipeDAOFindRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.slug, "test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.duration.getTime(), 18600000);
                assertEquals(recipe.difficulty, RecipeDifficulty.EASY);
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAONotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(0);
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOFindBySlugRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.findBySlug("test");
                assertEquals(recipe.id.intValue(), 1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOFindBySlugAndIdRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.findBySlugAndId("test", 2);
                assertEquals(recipe.id.intValue(), 1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                recipe = recipeDAO.findBySlugAndId("test-2", 2);
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAONotFoundBySlugRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.findBySlug("not-found");
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOFindByOwnerRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.findByOwner("test@testing.dev", 1);
                assertEquals(recipe.id.intValue(), 1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOFindByOwnerRecipeAdmin() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.findByOwner("admin@admin.dev", 1);
                assertEquals(recipe.id.intValue(), 1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.steps, "Description test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text, "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAONotFoundByOwnerRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.findByOwner("", 1);
                assertNull(recipe);

                recipe = recipeDAO.findByOwner("test@testing.dev", 0);
                assertNull(recipe);

                recipe = recipeDAO.findByOwner("", 0);
                assertNull(recipe);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOFindAllRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Recipe> recipes = recipeDAO.all();
                long count = recipeDAO.count();
                assertEquals(count, 2);

                assertEquals(recipes.get(0).title, "Test");

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOPageRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Recipe> recipes = recipeDAO.paginate(0, 1);
                assertEquals(recipes.get(0).title, "Test");
                assertEquals(recipes.size(), 1);

                recipes = recipeDAO.paginate(1, 1);
                assertEquals(recipes.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOSearchRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Recipe> recipes = recipeDAO.paginate(0, 1, "Test 2", null);
                long count = recipeDAO.count("Test 2", null);
                assertEquals(recipes.get(0).title, "Test 2");
                assertEquals(recipes.size(), 1);
                assertEquals(count, 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOCreateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe create = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0, userDAO.find(1), null, RecipeVisibility.PUBLIC);
                Recipe recipe = recipeDAO.create(create);
                assertEquals(recipe, create);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOUpdateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                recipe.title = "Update test";
                Recipe update = recipeDAO.update(recipe);
                assertEquals(update.title, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                long count = recipeDAO.count();
                assertEquals(count, 2);

                recipeDAO.delete(recipe);

                count = recipeDAO.count();
                assertEquals(count, 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(0);

                try {
                    recipeDAO.delete(recipe);
                } catch (Exception e) {
                }

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0,
                        userDAO.find(1), null, RecipeVisibility.PUBLIC);
                recipe = recipeDAO.create(recipe);
                Tag tag = tagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 0);

                RecipeDAO.addTags(Collections.singletonList(tag), recipe);

                assertEquals(tag.recipes.size(), 2);
                assertEquals(recipe.tags.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0,
                        userDAO.find(1), null, RecipeVisibility.PUBLIC);
                recipe = recipeDAO.create(recipe);
                Tag tag = tagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 0);

                RecipeDAO.addTag(tag, recipe);

                assertEquals(tag.recipes.size(), 2);
                assertEquals(recipe.tags.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                assertEquals(recipe.tags.size(), 1);

                RecipeDAO.deleteTags(recipe);

                assertEquals(recipe.tags.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                Tag tag = tagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 1);

                RecipeDAO.deleteTag(tag, recipe);

                assertEquals(tag.recipes.size(), 0);
                assertEquals(recipe.tags.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0, user, null, RecipeVisibility.PUBLIC);
                recipe = recipeDAO.create(recipe);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 0);

                RecipeDAO.addFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 2);
                assertEquals(recipe.favorites.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                RecipeDAO.deleteFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 0);
                assertEquals(recipe.favorites.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0, user, null, RecipeVisibility.PUBLIC);
                recipe = recipeDAO.create(recipe);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.ratings.size(), 0);

                RecipeDAO.addRating(user, recipe, 4.3);

                assertEquals(user.ratings.size(), 2);
                assertEquals(recipe.ratings.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(recipe.ratings.get(0).rating, new Double(2.5));

                RecipeDAO.updateRating(user, recipe, 4.3);

                assertEquals(recipe.ratings.get(0).rating, new Double(4.3));

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                RecipeDAO.deleteRating(user, recipe);

                assertEquals(user.ratings.size(), 0);
                assertEquals(recipe.ratings.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = categoryDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0,
                        userDAO.find(1), null, RecipeVisibility.PUBLIC);
                recipe = recipeDAO.create(recipe);

                assertEquals(category.recipes.size(), 1);
                assertNull(recipe.category);

                RecipeDAO.addOrUpdateCategory(category, recipe);

                assertEquals(category.recipes.size(), 2);
                assertEquals(recipe.category.text, "test");

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOUpdateCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = categoryDAO.find(1);
                Category newCategory = categoryDAO.find(2);
                Recipe recipe = new Recipe("test2", "Test2", null, new Date(), RecipeDifficulty.EASY, 0,
                        userDAO.find(1), category, RecipeVisibility.PUBLIC);
                recipe = recipeDAO.create(recipe);

                assertEquals(category.recipes.size(), 2);
                assertEquals(newCategory.recipes.size(), 1);
                assertEquals(recipe.category.text, "test");

                RecipeDAO.addOrUpdateCategory(newCategory, recipe);

                assertEquals(category.recipes.size(), 1);
                assertEquals(newCategory.recipes.size(), 2);
                assertEquals(recipe.category.text, "test-new");

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = categoryDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(category.recipes.size(), 1);
                assertEquals(recipe.category.text, "test");

                RecipeDAO.deleteCategory(recipe);

                assertEquals(category.recipes.size(), 0);
                assertNull(recipe.category);

                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddIngredient() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                assertEquals(recipe.ingredients.size(), 0);
                recipe.ingredients.add(new Ingredient("Test", null));
                recipeDAO.addIngredients(recipe);
                assertEquals(recipe.ingredients.size(), 1);

                successTest();
            });
        });
    }

}
