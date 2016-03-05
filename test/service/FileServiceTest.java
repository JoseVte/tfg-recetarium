package service;

import models.File;
import models.service.FileService;
import models.service.UserService;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class FileServiceTest extends AbstractTest {

    @Test
    public void testFileServiceFindFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = FileService.find(1);
                assertEquals(file.title, "test");
                assertEquals(file.user.id.intValue(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceNotFoundFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = FileService.find(0);
                assertNull(file);

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceFindAllFiles() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<File> file = FileService.all();
                long count = FileService.count();
                assertEquals(count, 1);

                assertEquals(file.get(0).title, "test");

                successTest();
            });
        });
    }

    @Test
    public void testFileServicePageFiles() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<File> file = FileService.paginate(0, 1);
                assertEquals(file.get(0).title, "test");
                assertEquals(file.size(), 1);

                file = FileService.paginate(1, 1);
                assertEquals(file.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceCreateFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File create = new File("test2", "", "test2", "test2", UserService.find(1));
                File file = FileService.create(create);
                assertEquals(file, create);

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceUpdateFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = FileService.find(1);
                file.title = "Update test";
                File update = FileService.update(file);
                assertEquals(update.title, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceDeleteFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = FileService.count();
                assertEquals(count, 1);

                assertTrue(FileService.delete(FileService.find(1), UserService.find(1)));

                count = FileService.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceDeleteFileAdmin() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = FileService.count();
                assertEquals(count, 1);

                assertTrue(FileService.delete(FileService.find(1), UserService.find(2)));

                count = FileService.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testFileServiceDeleteNotFoundFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(FileService.delete(null, UserService.find(1)));
                assertFalse(FileService.delete(FileService.find(1), null));
                assertFalse(FileService.delete(null, null));

                successTest();
            });
        });
    }
}
