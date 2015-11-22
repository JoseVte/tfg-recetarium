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

import models.Category;
import models.service.CategoryService;
import play.db.jpa.JPA;
import util.AbstractTest;

public class CategoryServiceTest extends AbstractTest {

    @Test
    public void testCategoryServiceFindSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryService.find(1);
                assertEquals(category.text, "test");
                assertEquals(category.recipes.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServiceNotFoundSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryService.find(0);
                assertNull(category);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServiceFindAllSections() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Category> categorys = CategoryService.all();
                long count = CategoryService.count();
                assertEquals(count, 2);

                assertEquals(categorys.get(0).text, "test");
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServicePageSections() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Category> categorys = CategoryService.paginate(0, 1);
                assertEquals(categorys.get(0).text, "test");
                assertEquals(categorys.size(), 1);

                categorys = CategoryService.paginate(1, 1);
                assertEquals(categorys.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServiceCreateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category create = new Category("test2");
                Category category = CategoryService.create(create);
                assertEquals(category, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServiceUpdateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Category category = CategoryService.find(1);
                category.text = "Update test";
                Category update = CategoryService.update(category);
                assertEquals(update.text, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServiceDeleteSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = CategoryService.count();
                assertEquals(count, 2);

                assertTrue(CategoryService.delete(1));

                count = CategoryService.count();
                assertEquals(count, 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCategoryServiceDeleteNotFoundSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(CategoryService.delete(0));
            
                successTest();
            });
        });
    }
}
