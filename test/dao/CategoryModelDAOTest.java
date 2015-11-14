package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Category;
import models.dao.CategoryDAO;
import play.db.jpa.JPA;
import util.AbstractTest;

public class CategoryModelDAOTest extends AbstractTest {

    @Test
    public void testCategoryDAOFindCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(1);
                assertEquals(category.text, "test");
                assertEquals(category.recipes.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAONotFoundCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(0);
                assertNull(category);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAOFindAllCategories() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Category> categories = CategoryDAO.all();
                long count = CategoryDAO.count();
                assertEquals(count, 2);

                assertEquals(categories.get(0).text, "test");
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAOPageCategories() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Category> categories = CategoryDAO.paginate(0, 1);
                assertEquals(categories.get(0).text, "test");
                assertEquals(categories.size(), 1);

                categories = CategoryDAO.paginate(1, 1);
                assertEquals(categories.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAOCreateCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category create = new Category("test2");
                Category category = CategoryDAO.create(create);
                assertEquals(category, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAOUpdateCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(1);
                category.text = "Update test";
                Category update = CategoryDAO.update(category);
                assertEquals(update.text, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAODeleteCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(1);
                long count = CategoryDAO.count();
                assertEquals(count, 2);

                CategoryDAO.delete(category);

                count = CategoryDAO.count();
                assertEquals(count, 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryDAODeleteNotFoundCategory() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryDAO.find(0);

                try {
                    CategoryDAO.delete(category);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }
}
