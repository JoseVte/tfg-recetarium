package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Media;
import play.db.jpa.JPA;
import util.AbstractTest;

public class MediaModelDAOTest extends AbstractTest {

    @Test
    public void testMediaDAOFindMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = mediaDAO.find(1);
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
                Media media = mediaDAO.find(0);
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
                List<Media> media = mediaDAO.all();
                long count = mediaDAO.count();
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
                List<Media> media = mediaDAO.paginate(0, 1);
                assertEquals(media.get(0).filename, "test");
                assertEquals(media.size(), 1);

                media = mediaDAO.paginate(1, 1);
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
                Media create = new Media("test2", recipeDAO.find(1));
                Media media = mediaDAO.create(create);
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
                Media media = mediaDAO.find(1);
                media.filename = "Update test";
                Media update = mediaDAO.update(media);
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
                Media media = mediaDAO.find(1);
                long count = mediaDAO.count();
                assertEquals(count, 1);

                mediaDAO.delete(media);

                count = mediaDAO.count();
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
                Media media = mediaDAO.find(0);

                try {
                    mediaDAO.delete(media);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }
}
