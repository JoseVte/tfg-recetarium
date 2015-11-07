package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import models.TypeUser;
import models.User;
import models.dao.RecipeDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class UserModelDAOTest extends WithApplication {

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
    public void testDAOFindUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                assertEquals(user.username, "test");
                assertEquals(user.email, "test@testing.dev");
                assertEquals(user.type, TypeUser.COMUN);
                assertEquals(user.recipes.size(), 1);

                User admin = UserDAO.find(2);
                assertEquals(admin.username, "admin");
                assertEquals(admin.email, "admin@admin.dev");
                assertEquals(admin.type, TypeUser.ADMIN);
                assertEquals(admin.recipes.size(), 0);
            });
        });
    }

    @Test
    public void testDAONotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(0);
                assertNull(user);
            });
        });
    }

    @Test
    public void testDAOFindAllUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<User> users = UserDAO.all();
                long count = UserDAO.count();
                assertEquals(count, 2);

                assertEquals(users.get(0).username, "test");
                assertEquals(users.get(1).username, "admin");
            });
        });
    }

    @Test
    public void testDAOPageUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<User> users = UserDAO.paginate(0, 1);
                assertEquals(users.get(0).username, "test");
                assertEquals(users.size(), 1);

                users = UserDAO.paginate(1, 1);
                assertEquals(users.get(0).username, "admin");
                assertEquals(users.size(), 1);
            });
        });
    }

    @Test
    public void testDAOCreateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User create = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                User user = UserDAO.create(create);
                assertEquals(user, create);
            });
        });
    }

    @Test
    public void testDAOUpdateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                user.username = "Update test";
                User update = UserDAO.update(user);
                assertEquals(update.username, "Update test");
            });
        });
    }

    @Test
    public void testDAODeleteUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                long count = UserDAO.count();
                assertEquals(count, 2);

                UserDAO.delete(user);

                count = UserDAO.count();
                assertEquals(count, 1);
            });
        });
    }

    @Test(expected = RuntimeException.class)
    public void testDAODeleteNotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(0);

                UserDAO.delete(user);
            });
        });
    }

    @Test
    public void testDAOAddFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                User friend = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                friend = UserDAO.create(friend);

                assertEquals(user.myFriends.size(), 1);
                assertEquals(friend.friends.size(), 0);

                UserDAO.addFriend(user, friend);

                assertEquals(user.myFriends.size(), 2);
                assertEquals(friend.friends.size(), 1);
            });
        });
    }

    @Test
    public void testDAODeleteFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                User admin = UserDAO.find(2);

                assertEquals(user.myFriends.size(), 1);
                assertEquals(admin.friends.size(), 1);

                UserDAO.deleteFriend(user, admin);

                assertEquals(user.myFriends.size(), 0);
                assertEquals(admin.friends.size(), 0);
            });
        });
    }

    @Test
    public void testDAOAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = UserDAO.create(user);

                assertEquals(user.recipesFavorites.size(), 0);
                assertEquals(recipe.favorites.size(), 1);

                RecipeDAO.addFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 2);
            });
        });
    }

    @Test
    public void testDAODeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                UserDAO.deleteFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 0);
                assertEquals(recipe.favorites.size(), 0);
            });
        });
    }

    @Test
    public void testDAOAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Recipe recipe = RecipeDAO.find(1);
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = UserDAO.create(user);

                assertEquals(user.ratings.size(), 0);
                assertEquals(recipe.ratings.size(), 1);

                RecipeDAO.addRating(user, recipe, 4.3);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.ratings.size(), 2);
            });
        });
    }

    @Test
    public void testDAOUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(recipe.ratings.get(0).rating, new Double(2.5));

                UserDAO.updateRating(user, recipe, 4.3);

                assertEquals(recipe.ratings.get(0).rating, new Double(4.3));
            });
        });
    }

    @Test
    public void testDAODeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                Recipe recipe = RecipeDAO.find(1);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                UserDAO.deleteRating(user, recipe);

                assertEquals(user.ratings.size(), 0);
                assertEquals(recipe.ratings.size(), 0);
            });
        });
    }
}
