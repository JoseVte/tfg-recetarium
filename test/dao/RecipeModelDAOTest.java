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
import models.dao.RecipeDAO;
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
}
