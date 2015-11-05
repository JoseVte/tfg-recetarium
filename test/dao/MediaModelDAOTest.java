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

import models.Media;
import models.dao.MediaDAO;
import models.dao.RecipeDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class MediaModelDAOTest extends WithApplication {

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
    public void testDAOFindMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaDAO.find(1);
                assertEquals(media.filename, "test");
                assertEquals(media.recipe.id.intValue(), 1);
            });
        });
    }

    @Test
    public void testDAONotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaDAO.find(0);
                assertNull(media);
            });
        });
    }

    @Test
    public void testDAOFindAllMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Media> medias = MediaDAO.all();
                long count = MediaDAO.count();
                assertEquals(count, 1);

                assertEquals(medias.get(0).filename, "test");
            });
        });
    }

    @Test
    public void testDAOPageMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Media> medias = MediaDAO.paginate(0, 1);
                assertEquals(medias.get(0).filename, "test");
                assertEquals(medias.size(), 1);

                medias = MediaDAO.paginate(1, 1);
                assertEquals(medias.size(), 0);
            });
        });
    }

    @Test
    public void testDAOCreateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media create = new Media("test2", RecipeDAO.find(1));
                Media media = MediaDAO.create(create);
                assertEquals(media, create);
            });
        });
    }

    @Test
    public void testDAOUpdateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaDAO.find(1);
                media.filename = "Update test";
                Media update = MediaDAO.update(media);
                assertEquals(update.filename, "Update test");
            });
        });
    }

    @Test
    public void testDAODeleteMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaDAO.find(1);
                long count = MediaDAO.count();
                assertEquals(count, 1);

                MediaDAO.delete(media);

                count = MediaDAO.count();
                assertEquals(count, 0);
            });
        });
    }

    @Test(expected = RuntimeException.class)
    public void testDAODeleteNotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Media media = MediaDAO.find(0);

                MediaDAO.delete(media);
            });
        });
    }
}
