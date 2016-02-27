package dao;

import models.File;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.*;

public class FileModelDAOTest extends AbstractTest {

    @Test
    public void testFileDAOFindFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = fileDAO.find(1);
                assertEquals(file.url, "test");
                assertEquals(file.user.id.intValue(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testFileDAONotFoundFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = fileDAO.find(0);
                assertNull(file);

                successTest();
            });
        });
    }

    @Test
    public void testFileDAOFindAllFiles() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<File> file = fileDAO.all();
                long count = fileDAO.count();
                assertEquals(count, 1);

                assertEquals(file.get(0).url, "test");

                successTest();
            });
        });
    }

    @Test
    public void testFileDAOPageFiles() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<File> file = fileDAO.paginate(0, 1);
                assertEquals(file.get(0).url, "test");
                assertEquals(file.size(), 1);

                file = fileDAO.paginate(1, 1);
                assertEquals(file.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testFileDAOCreateFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File create = new File("test2", "image", "test2", userDAO.find(1));
                File file = fileDAO.create(create);
                assertEquals(file, create);

                successTest();
            });
        });
    }

    @Test
    public void testFileDAOUpdateFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = fileDAO.find(1);
                file.url = "Update test";
                File update = fileDAO.update(file);
                assertEquals(update.url, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testFileDAODeleteFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = fileDAO.find(1);
                long count = fileDAO.count();
                assertEquals(count, 1);

                fileDAO.delete(file);

                count = fileDAO.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testFileDAODeleteNotFoundFile() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                File file = fileDAO.find(0);

                try {
                    fileDAO.delete(file);
                } catch (Exception e) {
                }

                successTest();
            });
        });
    }
}
