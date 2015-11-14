package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Media;
import models.service.MediaService;
import models.service.RecipeService;
import play.db.jpa.JPA;
import util.AbstractTest;

public class MediaServiceTest extends AbstractTest {

    @Test
    public void testMediaServiceFindMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaService.find(1);
                assertEquals(media.filename, "test");
                assertEquals(media.recipe.id.intValue(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServiceNotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaService.find(0);
                assertNull(media);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServiceFindAllMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Media> media = MediaService.all();
                long count = MediaService.count();
                assertEquals(count, 1);

                assertEquals(media.get(0).filename, "test");
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServicePageMedias() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Media> media = MediaService.paginate(0, 1);
                assertEquals(media.get(0).filename, "test");
                assertEquals(media.size(), 1);

                media = MediaService.paginate(1, 1);
                assertEquals(media.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServiceCreateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media create = new Media("test2", RecipeService.find(1));
                Media media = MediaService.create(create);
                assertEquals(media, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServiceUpdateMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Media media = MediaService.find(1);
                media.filename = "Update test";
                Media update = MediaService.update(media);
                assertEquals(update.filename, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServiceDeleteMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = MediaService.count();
                assertEquals(count, 1);

                assertTrue(MediaService.delete(1));

                count = MediaService.count();
                assertEquals(count, 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testMediaServiceDeleteNotFoundMedia() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(MediaService.delete(0));
            
                successTest();
            });
        });
    }
}
