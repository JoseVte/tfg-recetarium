package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import models.Recipe;
import models.service.RecipeService;
import models.service.UserService;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class RecipeServiceTest extends WithApplication {

    @Override
    public FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    public void initializeData() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("memoryPersistenceUnit");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {

            // Start the transaction
            trx.begin();
            InitDataLoader.load(em, "test/init-data.yml");
            // Commit and end the transaction
            trx.commit();
        } catch (RuntimeException | IOException e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
            }
            throw e;
        } finally {
            // Close the manager
            em.close();
            emf.close();
        }
    }

    @Test
    public void testServiceFindRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeService.find(1);
                assertEquals(recipe.title, "Test");
                assertEquals(recipe.slug, "test");
                assertEquals(recipe.description, "Descripcion test");
                assertEquals(recipe.user.id.intValue(), 1);
                assertEquals(recipe.section.text.toString(), "test");
                assertEquals(recipe.media.size(), 1);
                assertEquals(recipe.tags.size(), 1);
                assertEquals(recipe.favorites.size(), 1);
                assertEquals(recipe.ratings.size(), 1);
            });
        });
    }

    @Test
    public void testServiceNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeService.find(0);
                assertNull(recipe);
            });
        });
    }

    @Test
    public void testServiceFindAllRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Recipe> recipes = RecipeService.all();
                long count = RecipeService.count();
                assertEquals(count, 1);

                assertEquals(recipes.get(0).title, "Test");
            });
        });
    }

    @Test
    public void testServicePageRecipes() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Recipe> recipes = RecipeService.paginate(0, 1);
                assertEquals(recipes.get(0).title, "Test");
                assertEquals(recipes.size(), 1);

                recipes = RecipeService.paginate(1, 1);
                assertEquals(recipes.size(), 0);
            });
        });
    }

    @Test
    public void testServiceCreateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe create = new Recipe("test2", "Test2", null, UserService.find(1));
                Recipe recipe = RecipeService.create(create);
                assertEquals(recipe, create);
            });
        });
    }

    @Test
    public void testServiceUpdateRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeService.find(1);
                recipe.title = "Update test";
                Recipe update = RecipeService.update(recipe);
                assertEquals(update.title, "Update test");
            });
        });
    }

    @Test
    public void testServiceDeleteRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                long count = RecipeService.count();
                assertEquals(count, 1);

                assertTrue(RecipeService.delete(1));

                count = RecipeService.count();
                assertEquals(count, 0);
            });
        });
    }

    public void testServiceDeleteNotFoundRecipe() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                assertFalse(RecipeService.delete(0));
            });
        });
    }

    @Test
    public void testServiceAddTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = new Recipe("test2", "Test2", null, UserService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addTag(1, recipe.id));
            });
        });
    }

    @Test
    public void testServiceAddTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addTag(1, 0));
                assertFalse(RecipeService.addTag(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddTagAlreadyTagged() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addTag(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteTag() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(RecipeService.deleteTag(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteTagNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.deleteTag(1, 0));
                assertFalse(RecipeService.deleteTag(0, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteTagNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = new Recipe("test2", "Test2", null, UserService.find(1));
                recipe = RecipeService.create(recipe);

                assertFalse(RecipeService.deleteTag(1, recipe.id));
            });
        });
    }

    @Test
    public void testServiceAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = new Recipe("test2", "Test2", null, UserService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addFavorite(recipe.id, 1));
            });
        });
    }

    @Test
    public void testServiceAddFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addFavorite(1, 0));
                assertFalse(RecipeService.addFavorite(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddFavoriteAlredyFav() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addFavorite(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(RecipeService.deleteFavorite(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.deleteFavorite(1, 0));
                assertFalse(RecipeService.deleteFavorite(0, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFavoriteNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.deleteFavorite(2, 1));
            });
        });
    }

    @Test
    public void testServiceAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = new Recipe("test2", "Test2", null, UserService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addRating(recipe.id, 1, 4.3));
            });
        });
    }

    @Test
    public void testServiceAddRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addRating(1, 0, 0.0));
                assertFalse(RecipeService.addRating(0, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceAddRatingAlredyRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addRating(1, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceAddRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addRating(1, 1, -0.01));
                assertFalse(RecipeService.addRating(1, 1, 5.01));
            });
        });
    }

    @Test
    public void testServiceUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(RecipeService.updateRating(1, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceUpdateRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.updateRating(1, 0, 0.0));
                assertFalse(RecipeService.updateRating(0, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceUpdateRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.updateRating(1, 1, -0.01));
                assertFalse(RecipeService.updateRating(1, 1, 5.01));
            });
        });
    }

    @Test
    public void testServiceDeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(RecipeService.deleteRating(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.deleteRating(1, 0));
                assertFalse(RecipeService.deleteRating(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = new Recipe("test2", "Test2", null, UserService.find(1));
                recipe = RecipeService.create(recipe);

                assertTrue(RecipeService.addSection(recipe.id, 1));
            });
        });
    }

    @Test
    public void testServiceAddSectionNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addSection(1, 0));
                assertFalse(RecipeService.addSection(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddSectionAlredySection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.addSection(1, 1));
            });
        });
    }

    @Test
    public void testServiceUpdateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(RecipeService.updateSection(1, 1));
                assertTrue(RecipeService.updateSection(2, 1));
            });
        });
    }

    @Test
    public void testServiceUpdateSectionNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.updateSection(1, 0));
                assertFalse(RecipeService.updateSection(0, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(RecipeService.deleteSection(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteSectionNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(RecipeService.deleteSection(1, 0));
                assertFalse(RecipeService.deleteSection(0, 1));
            });
        });
    }
}
