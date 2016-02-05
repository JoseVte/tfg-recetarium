package service;

import models.Tag;
import models.service.TagService;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class TagServiceTest extends AbstractTest {

    @Test
    public void testTagServiceFindTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = TagService.find(1);
                assertEquals(tag.text, "test");
                assertEquals(tag.recipes.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceNotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = TagService.find(0);
                assertNull(tag);

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceFindAllTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Tag> tags = TagService.all();
                long count = TagService.count();
                assertEquals(count, 1);

                assertEquals(tags.get(0).text, "test");

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceSearchTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Tag> tags = TagService.search("test");
                assertEquals(tags.size(), 1);
                assertEquals(tags.get(0).text, "test");

                tags = TagService.search("no exist");

                successTest();
            });
        });
    }

    @Test
    public void testTagServicePageTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Tag> tags = TagService.paginate(0, 1);
                assertEquals(tags.get(0).text, "test");
                assertEquals(tags.size(), 1);

                tags = TagService.paginate(1, 1);
                assertEquals(tags.size(), 0);

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceCreateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag create = new Tag("test2");
                Tag tag = TagService.create(create);
                assertEquals(tag, create);

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceCreateTags() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                List<String> tagsNames = Arrays.asList("Test", "Test 2");
                List<Integer> ids = TagService.create(tagsNames);
                assertEquals(ids.size(), 2);

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceUpdateTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = TagService.find(1);
                tag.text = "Update test";
                Tag update = TagService.update(tag);
                assertEquals(update.text, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceDeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = TagService.count();
                assertEquals(count, 1);

                assertTrue(TagService.delete(1));

                count = TagService.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceDeleteNotFoundTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(TagService.delete(0));

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = new Tag("test2");
                tag = TagService.create(tag);

                assertTrue(TagService.addRecipe(tag.id, 1));

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceAddTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(TagService.addRecipe(1, 0));
                assertFalse(TagService.addRecipe(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceAddTagAlreadyTagged() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(TagService.addRecipe(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceDeleteRecipeTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(TagService.deleteRecipe(1, 1));

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceDeleteRecipeTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(TagService.deleteRecipe(1, 0));
                assertFalse(TagService.deleteRecipe(0, 1));

                successTest();
            });
        });
    }

    @Test
    public void testTagServiceDeleteRecipeTagNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Tag tag = new Tag("test2");
                tag = TagService.create(tag);

                assertFalse(TagService.deleteRecipe(tag.id, 1));

                successTest();
            });
        });
    }
}
