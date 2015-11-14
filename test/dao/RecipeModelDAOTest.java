package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Category;
import models.Recipe;
import models.Tag;
import models.User;
import models.dao.CategoryDAO;
import models.dao.RecipeDAO;
import models.dao.TagDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import util.AbstractTest;

public class RecipeModelDAOTest extends AbstractTest {

    @Test
    public void testRecipeDAOFindRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeDAO.find(1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.slug, "test");
                assertEquals(recipe.description, "Descripcion test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.category.text.toString(), "test");
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
                Recipe recipe = RecipeDAO.find(0);
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
                List<Recipe> recipes = RecipeDAO.all();
                long count = RecipeDAO.count();
                assertEquals(count, 1);

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
                List<Recipe> recipes = RecipeDAO.paginate(0, 1);
                assertEquals(recipes.get(0).title, "Test");
                assertEquals(recipes.size(), 1);

                recipes = RecipeDAO.paginate(1, 1);
                assertEquals(recipes.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOCreateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe create = new Recipe("test2", "Test2", null, UserDAO.find(1));
                Recipe recipe = RecipeDAO.create(create);
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
                Recipe recipe = RecipeDAO.find(1);
                recipe.title = "Update test";
                Recipe update = RecipeDAO.update(recipe);
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
                Recipe recipe = RecipeDAO.find(1);
                long count = RecipeDAO.count();
                assertEquals(count, 1);

                RecipeDAO.delete(recipe);

                count = RecipeDAO.count();
                assertEquals(count, 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeDAO.find(0);

                try {
                    RecipeDAO.delete(recipe);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAOAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);
                Tag tag = TagDAO.find(1);

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
    public void testRecipeDAODeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = RecipeDAO.find(1);
                Tag tag = TagDAO.find(1);

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
                User user = UserDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);

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
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

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
                User user = UserDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);

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
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

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
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

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
    public void testRecipeDAOAddSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);

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
    public void testRecipeDAOUpdateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(1);
                Category newSection = CategoryDAO.find(2);
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1), category);
                recipe = RecipeDAO.create(recipe);

                assertEquals(category.recipes.size(), 2);
                assertEquals(recipe.category.text, "test");

                RecipeDAO.addOrUpdateCategory(newSection, recipe);

                assertEquals(newSection.recipes.size(), 1);
                assertEquals(recipe.category.text, "test-new");
            
                successTest();
            });
        });
    }

    @Test
    public void testRecipeDAODeleteSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(category.recipes.size(), 1);
                assertEquals(recipe.category.text, "test");

                RecipeDAO.deleteCategory(recipe);

                assertEquals(category.recipes.size(), 0);
                assertNull(recipe.category);
            
                successTest();
            });
        });
    }
}
