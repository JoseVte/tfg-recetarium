package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Recipe;
import models.Tag;
import models.dao.TagDAO;
import play.db.jpa.JPA;
import util.AbstractTest;

public class TagModelDAOTest extends AbstractTest {

    @Test
    public void testTagDAOFindTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = tagDAO.find(1);
                assertEquals(tag.text, "test");
                assertEquals(tag.recipes.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAONotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = tagDAO.find(0);
                assertNull(tag);
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAOFindAllTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Tag> tags = tagDAO.all();
                long count = tagDAO.count();
                assertEquals(count, 1);

                assertEquals(tags.get(0).text, "test");
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAOPageTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Tag> tags = tagDAO.paginate(0, 1);
                assertEquals(tags.get(0).text, "test");
                assertEquals(tags.size(), 1);

                tags = tagDAO.paginate(1, 1);
                assertEquals(tags.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAOCreateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag create = new Tag("test2");
                Tag tag = tagDAO.create(create);
                assertEquals(tag, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAOUpdateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = tagDAO.find(1);
                tag.text = "Update test";
                Tag update = tagDAO.update(tag);
                assertEquals(update.text, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAODeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = tagDAO.find(1);
                long count = tagDAO.count();
                assertEquals(count, 1);

                tagDAO.delete(tag);

                count = tagDAO.count();
                assertEquals(count, 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAODeleteNotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = tagDAO.find(0);

                try {
                    tagDAO.delete(tag);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAOAddRecipeTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = new Tag("test2");
                tag = tagDAO.create(tag);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(tag.recipes.size(), 0);
                assertEquals(recipe.tags.size(), 1);

                TagDAO.addRecipe(tag, recipe);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 2);
            
                successTest();
            });
        });
    }

    @Test
    public void testTagDAODeleteRecipeTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                Tag tag = tagDAO.find(1);

                assertEquals(tag.recipes.size(), 1);
                assertEquals(recipe.tags.size(), 1);

                TagDAO.deleteRecipe(tag, recipe);

                assertEquals(tag.recipes.size(), 0);
                assertEquals(recipe.tags.size(), 0);
            
                successTest();
            });
        });
    }
}
