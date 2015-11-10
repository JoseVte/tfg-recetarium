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

import models.Media;
import models.service.MediaService;
import models.service.RecipeService;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class MediaServiceTest extends WithApplication {

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
    public void testServiceFindMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaService.find(1);
                assertEquals(media.filename, "test");
                assertEquals(media.recipe.id.intValue(), 1);
            });
        });
    }

    @Test
    public void testServiceNotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaService.find(0);
                assertNull(media);
            });
        });
    }

    @Test
    public void testServiceFindAllMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Media> media = MediaService.all();
                long count = MediaService.count();
                assertEquals(count, 1);

                assertEquals(media.get(0).filename, "test");
            });
        });
    }

    @Test
    public void testServicePageMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Media> media = MediaService.paginate(0, 1);
                assertEquals(media.get(0).filename, "test");
                assertEquals(media.size(), 1);

                media = MediaService.paginate(1, 1);
                assertEquals(media.size(), 0);
            });
        });
    }

    @Test
    public void testServiceCreateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media create = new Media("test2", RecipeService.find(1));
                Media media = MediaService.create(create);
                assertEquals(media, create);
            });
        });
    }

    @Test
    public void testServiceUpdateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaService.find(1);
                media.filename = "Update test";
                Media update = MediaService.update(media);
                assertEquals(update.filename, "Update test");
            });
        });
    }

    @Test
    public void testServiceDeleteMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                long count = MediaService.count();
                assertEquals(count, 1);

                assertTrue(MediaService.delete(1));

                count = MediaService.count();
                assertEquals(count, 0);
            });
        });
    }

    public void testServiceDeleteNotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                assertFalse(MediaService.delete(0));
            });
        });
    }
}
