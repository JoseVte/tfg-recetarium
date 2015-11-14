package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Media;
import models.dao.MediaDAO;
import models.dao.RecipeDAO;
import play.db.jpa.JPA;
import util.AbstractTest;

public class MediaModelDAOTest extends AbstractTest {

    @Test
    public void testMediaDAOFindMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaDAO.find(1);
                assertEquals(media.filename, "test");
                assertEquals(media.recipe.id.intValue(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAONotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaDAO.find(0);
                assertNull(media);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAOFindAllMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Media> media = MediaDAO.all();
                long count = MediaDAO.count();
                assertEquals(count, 1);

                assertEquals(media.get(0).filename, "test");
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAOPageMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Media> media = MediaDAO.paginate(0, 1);
                assertEquals(media.get(0).filename, "test");
                assertEquals(media.size(), 1);

                media = MediaDAO.paginate(1, 1);
                assertEquals(media.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAOCreateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media create = new Media("test2", RecipeDAO.find(1));
                Media media = MediaDAO.create(create);
                assertEquals(media, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAOUpdateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaDAO.find(1);
                media.filename = "Update test";
                Media update = MediaDAO.update(media);
                assertEquals(update.filename, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAODeleteMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaDAO.find(1);
                long count = MediaDAO.count();
                assertEquals(count, 1);

                MediaDAO.delete(media);

                count = MediaDAO.count();
                assertEquals(count, 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaDAODeleteNotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaDAO.find(0);

                try {
                    MediaDAO.delete(media);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }
}
