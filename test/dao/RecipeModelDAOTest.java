package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import models.Recipe;
import models.Tag;
import models.TypeUser;
import models.User;
import models.dao.RecipeDAO;
import models.dao.TagDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class RecipeModelDAOTest extends WithApplication {

    @Override
    public FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    public void initializeData() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("memoryPersistenceUnit");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {

            // Start the transaction
            trx.begin();
            InitDataLoader.load(em, "test/init-data.yml");
            // Commit and end the transaction
            trx.commit();
        } catch (RuntimeException | IOException e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
            }
            throw e;
        } finally {
            // Close the manager
            em.close();
            emf.close();
        }
    }

    @Test
    public void testDAOFindRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.slug, "test");
                assertEquals(recipe.description, "Descripcion test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.section.text.toString(), "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);
            });
        });
    }

    @Test
    public void testDAONotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(0);
                assertNull(recipe);
            });
        });
    }

    @Test
    public void testDAOFindAllRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Recipe> recipes = RecipeDAO.all();
                long count = RecipeDAO.count();
                assertEquals(count, 1);

                assertEquals(recipes.get(0).title, "Test");
            });
        });
    }

    @Test
    public void testDAOPageRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Recipe> recipes = RecipeDAO.paginate(0, 1);
                assertEquals(recipes.get(0).title, "Test");
                assertEquals(recipes.size(), 1);

                recipes = RecipeDAO.paginate(1, 1);
                assertEquals(recipes.size(), 0);
            });
        });
    }

    @Test
    public void testDAOCreateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe create = new Recipe("test2", "Test2", null, UserDAO.find(1));
                Recipe recipe = RecipeDAO.create(create);
                assertEquals(recipe, create);
            });
        });
    }

    @Test
    public void testDAOUpdateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                recipe.title = "Update test";
                Recipe update = RecipeDAO.update(recipe);
                assertEquals(update.title, "Update test");
            });
        });
    }

    @Test
    public void testDAODeleteRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                long count = RecipeDAO.count();
                assertEquals(count, 1);

                RecipeDAO.delete(recipe);

                count = RecipeDAO.count();
                assertEquals(count, 0);
            });
        });
    }

    @Test(expected = RuntimeException.class)
    public void testDAODeleteNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(0);

                RecipeDAO.delete(recipe);
            });
        });
    }

    @Test
    public void testDAOAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);
                Tag tag = TagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 0);

                RecipeDAO.addTag(tag, recipe);

                assertEquals(tag.recipes.size(), 2);
                assertEquals(recipe.tags.size(), 1);
            });
        });
    }

    @Test
    public void testDAODeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                Tag tag = TagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 1);

                RecipeDAO.deleteTag(tag, recipe);

                assertEquals(tag.recipes.size(), 0);
                assertEquals(recipe.tags.size(), 0);
            });
        });
    }

    @Test
    public void testDAOAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 0);

                UserDAO.addFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 2);
                assertEquals(recipe.favorites.size(), 1);
            });
        });
    }

    @Test
    public void testDAODeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                RecipeDAO.deleteFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 0);
                assertEquals(recipe.favorites.size(), 0);
            });
        });
    }

    @Test
    public void testDAOAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = new Recipe("test2", "Test2", null, UserDAO.find(1));
                recipe = RecipeDAO.create(recipe);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.ratings.size(), 0);

                UserDAO.addRating(user, recipe, 4.3);

                assertEquals(user.ratings.size(), 2);
                assertEquals(recipe.ratings.size(), 1);
            });
        });
    }

    @Test
    public void testDAOUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(recipe.ratings.get(0).rating, new Double(2.5));

                RecipeDAO.updateRating(user, recipe, 4.3);

                assertEquals(recipe.ratings.get(0).rating, new Double(4.3));
            });
        });
    }

    @Test
    public void testDAODeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                RecipeDAO.deleteRating(user, recipe);

                assertEquals(user.ratings.size(), 0);
                assertEquals(recipe.ratings.size(), 0);
            });
        });
    }
}
