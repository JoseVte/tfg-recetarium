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

import models.TypeUser;
import models.User;
import models.service.UserService;
import play.db.jpa.JPA;
import util.AbstractTest;

public class UserServiceTest extends AbstractTest {

    @Test
    public void testUserServiceFindUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = UserService.find(1);
                assertEquals(user.username, "test");
                assertEquals(user.email, "test@testing.dev");
                assertEquals(user.type, TypeUser.COMUN);
                assertEquals(user.recipes.size(), 1);

                User admin = UserService.find(2);
                assertEquals(admin.username, "admin");
                assertEquals(admin.email, "admin@admin.dev");
                assertEquals(admin.type, TypeUser.ADMIN);
                assertEquals(admin.recipes.size(), 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceNotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = UserService.find(0);
                assertNull(user);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceFindAllUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<User> users = UserService.all();
                long count = UserService.count();
                assertEquals(count, 2);

                assertEquals(users.get(0).username, "test");
                assertEquals(users.get(1).username, "admin");
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServicePageUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<User> users = UserService.paginate(0, 1);
                assertEquals(users.get(0).username, "test");
                assertEquals(users.size(), 1);

                users = UserService.paginate(1, 1);
                assertEquals(users.get(0).username, "admin");
                assertEquals(users.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceCreateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User create = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                User user = UserService.create(create);
                assertEquals(user, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceUpdateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = UserService.find(1);
                user.username = "Update test";
                User update = UserService.update(user);
                assertEquals(update.username, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = UserService.count();
                assertEquals(count, 2);

                assertTrue(UserService.delete(1));

                count = UserService.count();
                assertEquals(count, 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteNotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(UserService.delete(0));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User friend = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                friend = UserService.create(friend);

                assertTrue(UserService.addFriend(1, friend.id));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFriendNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addFriend(1, 0));
                assertFalse(UserService.addFriend(0, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFriendAlreadyFriends() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addFriend(1, 2));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFriendSameUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addFriend(1, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(UserService.deleteFriend(1, 2));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteFriendNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.deleteFriend(1, 0));
                assertFalse(UserService.deleteFriend(0, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteFriendNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User friend = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                friend = UserService.create(friend);

                assertFalse(UserService.deleteFriend(1, friend.id));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = UserService.create(user);

                assertTrue(UserService.addFavorite(user.id, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addFavorite(1, 0));
                assertFalse(UserService.addFavorite(0, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddFavoriteAlredyFav() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addFavorite(1, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(UserService.deleteFavorite(1, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.deleteFavorite(1, 0));
                assertFalse(UserService.deleteFavorite(0, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteFavoriteNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.deleteFavorite(2, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = UserService.create(user);

                assertTrue(UserService.addRating(user.id, 1, 4.3));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addRating(1, 0, 0.0));
                assertFalse(UserService.addRating(0, 1, 0.0));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddRatingAlredyRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addRating(1, 1, 0.0));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceAddRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.addRating(1, 1, -0.01));
                assertFalse(UserService.addRating(1, 1, 5.01));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(UserService.updateRating(1, 1, 0.0));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceUpdateRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.updateRating(1, 0, 0.0));
                assertFalse(UserService.updateRating(0, 1, 0.0));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceUpdateRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.updateRating(1, 1, -0.01));
                assertFalse(UserService.updateRating(1, 1, 5.01));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertTrue(UserService.deleteRating(1, 1));
            
                successTest();
            });
        });
    }

    @Test
    public void testUserServiceDeleteRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();

                assertFalse(UserService.deleteRating(1, 0));
                assertFalse(UserService.deleteRating(0, 1));
            
                successTest();
            });
        });
    }
}
