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
import models.dao.RecipeDAO;
import models.dao.TagDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class TagModelDAOTest extends WithApplication {

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
    public void testDAOFindTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagDAO.find(1);
                assertEquals(tag.text, "test");
                assertEquals(tag.recipes.size(), 1);
            });
        });
    }

    @Test
    public void testDAONotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagDAO.find(0);
                assertNull(tag);
            });
        });
    }

    @Test
    public void testDAOFindAllTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Tag> tags = TagDAO.all();
                long count = TagDAO.count();
                assertEquals(count, 1);

                assertEquals(tags.get(0).text, "test");
            });
        });
    }

    @Test
    public void testDAOPageTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Tag> tags = TagDAO.paginate(0, 1);
                assertEquals(tags.get(0).text, "test");
                assertEquals(tags.size(), 1);

                tags = TagDAO.paginate(1, 1);
                assertEquals(tags.size(), 0);
            });
        });
    }

    @Test
    public void testDAOCreateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag create = new Tag("test2");
                Tag tag = TagDAO.create(create);
                assertEquals(tag, create);
            });
        });
    }

    @Test
    public void testDAOUpdateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagDAO.find(1);
                tag.text = "Update test";
                Tag update = TagDAO.update(tag);
                assertEquals(update.text, "Update test");
            });
        });
    }

    @Test
    public void testDAODeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagDAO.find(1);
                long count = TagDAO.count();
                assertEquals(count, 1);

                TagDAO.delete(tag);

                count = TagDAO.count();
                assertEquals(count, 0);
            });
        });
    }

    @Test(expected = RuntimeException.class)
    public void testDAODeleteNotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagDAO.find(0);

                TagDAO.delete(tag);
            });
        });
    }

    @Test
    public void testDAOAddRecipeTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = new Tag("test2");
                tag = TagDAO.create(tag);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(tag.recipes.size(), 0);
                assertEquals(recipe.tags.size(), 1);

                TagDAO.addRecipe(tag, recipe);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 2);
            });
        });
    }

    @Test
    public void testDAODeleteRecipeTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                Tag tag = TagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 1);

                TagDAO.deleteRecipe(tag, recipe);

                assertEquals(tag.recipes.size(), 0);
                assertEquals(recipe.tags.size(), 0);
            });
        });
    }
}
