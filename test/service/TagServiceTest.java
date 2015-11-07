package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

import models.Tag;
import models.service.TagService;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class TagServiceTest extends WithApplication {

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
    public void testServiceFindTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagService.find(1);
                assertEquals(tag.text, "test");
                assertEquals(tag.recipes.size(), 1);
            });
        });
    }

    @Test
    public void testServiceNotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagService.find(0);
                assertNull(tag);
            });
        });
    }

    @Test
    public void testServiceFindAllTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Tag> tags = TagService.all();
                long count = TagService.count();
                assertEquals(count, 1);

                assertEquals(tags.get(0).text, "test");
            });
        });
    }

    @Test
    public void testServicePageTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Tag> tags = TagService.paginate(0, 1);
                assertEquals(tags.get(0).text, "test");
                assertEquals(tags.size(), 1);

                tags = TagService.paginate(1, 1);
                assertEquals(tags.size(), 0);
            });
        });
    }

    @Test
    public void testServiceCreateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag create = new Tag("test2");
                Tag tag = TagService.create(create);
                assertEquals(tag, create);
            });
        });
    }

    @Test
    public void testServiceUpdateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = TagService.find(1);
                tag.text = "Update test";
                Tag update = TagService.update(tag);
                assertEquals(update.text, "Update test");
            });
        });
    }

    @Test
    public void testServiceDeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                long count = TagService.count();
                assertEquals(count, 1);

                assertTrue(TagService.delete(1));

                count = TagService.count();
                assertEquals(count, 0);
            });
        });
    }

    public void testServiceDeleteNotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                assertFalse(TagService.delete(0));
            });
        });
    }

    @Test
    public void testServiceAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = new Tag("test2");
                tag = TagService.create(tag);

                assertTrue(TagService.addRecipe(tag.id, 1));
            });
        });
    }

    @Test
    public void testServiceAddTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(TagService.addRecipe(1, 0));
                assertFalse(TagService.addRecipe(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddTagAlreadyTagged() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(TagService.addRecipe(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteRecipeTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(TagService.deleteRecipe(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteRecipeTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(TagService.deleteRecipe(1, 0));
                assertFalse(TagService.deleteRecipe(0, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteRecipeTagNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Tag tag = new Tag("test2");
                tag = TagService.create(tag);

                assertFalse(TagService.deleteRecipe(tag.id, 1));
            });
        });
    }
}
