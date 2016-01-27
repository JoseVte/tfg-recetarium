package service;

import models.Category;
import models.service.CategoryService;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

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
                List<Category> categories = CategoryService.all();
                long count = CategoryService.count();
                assertEquals(count, 2);

                assertEquals(categories.get(0).text, "test");

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
