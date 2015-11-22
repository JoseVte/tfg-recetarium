package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Recipe;
import models.TypeUser;
import models.User;
import models.dao.RecipeDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import util.AbstractTest;

public class UserModelDAOTest extends AbstractTest {

    @Test
    public void testUserDAOFindAnUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                assertEquals(user.username, "test");
                assertEquals(user.email, "test@testing.dev");
                assertEquals(user.type, TypeUser.COMUN);
                assertEquals(user.recipes.size(), 1);

                User admin = userDAO.find(2);
                assertEquals(admin.username, "admin");
                assertEquals(admin.email, "admin@admin.dev");
                assertEquals(admin.type, TypeUser.ADMIN);
                assertEquals(admin.recipes.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAONotFoundAnUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(0);
                assertNull(user);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOFindAllUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<User> users = userDAO.all();
                long count = userDAO.count();
                assertEquals(count, 2);

                assertEquals(users.get(0).username, "test");
                assertEquals(users.get(1).username, "admin");
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOPageUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<User> users = userDAO.paginate(0, 1);
                assertEquals(users.get(0).username, "test");
                assertEquals(users.size(), 1);

                users = userDAO.paginate(1, 1);
                assertEquals(users.get(0).username, "admin");
                assertEquals(users.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOCreateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User create = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                User user = userDAO.create(create);
                assertEquals(user, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOUpdateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                user.username = "Update test";
                User update = userDAO.update(user);
                assertEquals(update.username, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAODeleteUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                long count = userDAO.count();
                assertEquals(count, 2);

                userDAO.delete(user);

                count = userDAO.count();
                assertEquals(count, 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAODeleteNotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(0);

                try {
                    userDAO.delete(user);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOAddFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                User friend = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                friend = userDAO.create(friend);

                assertEquals(user.myFriends.size(), 1);
                assertEquals(friend.friends.size(), 0);

                UserDAO.addFriend(user, friend);

                assertEquals(user.myFriends.size(), 2);
                assertEquals(friend.friends.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAODeleteFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                User admin = userDAO.find(2);

                assertEquals(user.myFriends.size(), 1);
                assertEquals(admin.friends.size(), 1);

                UserDAO.deleteFriend(user, admin);

                assertEquals(user.myFriends.size(), 0);
                assertEquals(admin.friends.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = userDAO.create(user);

                assertEquals(user.recipesFavorites.size(), 0);
                assertEquals(recipe.favorites.size(), 1);

                RecipeDAO.addFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 2);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAODeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(user.recipesFavorites.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                UserDAO.deleteFavorite(user, recipe);

                assertEquals(user.recipesFavorites.size(), 0);
                assertEquals(recipe.favorites.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Recipe recipe = recipeDAO.find(1);
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = userDAO.create(user);

                assertEquals(user.ratings.size(), 0);
                assertEquals(recipe.ratings.size(), 1);

                RecipeDAO.addRating(user, recipe, 4.3);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.ratings.size(), 2);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAOUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(recipe.ratings.get(0).rating, new Double(2.5));

                UserDAO.updateRating(user, recipe, 4.3);

                assertEquals(recipe.ratings.get(0).rating, new Double(4.3));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserDAODeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = userDAO.find(1);
                Recipe recipe = recipeDAO.find(1);

                assertEquals(user.ratings.size(), 1);
                assertEquals(recipe.favorites.size(), 1);

                UserDAO.deleteRating(user, recipe);

                assertEquals(user.ratings.size(), 0);
                assertEquals(recipe.ratings.size(), 0);
            
                successTest();
            });
        });
    }
}
