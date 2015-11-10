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

import models.TypeUser;
import models.User;
import models.service.UserService;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class UserServiceTest extends WithApplication {

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
    public void testServiceFindUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
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
            });
        });
    }

    @Test
    public void testServiceNotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserService.find(0);
                assertNull(user);
            });
        });
    }

    @Test
    public void testServiceFindAllUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<User> users = UserService.all();
                long count = UserService.count();
                assertEquals(count, 2);

                assertEquals(users.get(0).username, "test");
                assertEquals(users.get(1).username, "admin");
            });
        });
    }

    @Test
    public void testServicePageUsers() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<User> users = UserService.paginate(0, 1);
                assertEquals(users.get(0).username, "test");
                assertEquals(users.size(), 1);

                users = UserService.paginate(1, 1);
                assertEquals(users.get(0).username, "admin");
                assertEquals(users.size(), 1);
            });
        });
    }

    @Test
    public void testServiceCreateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User create = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                User user = UserService.create(create);
                assertEquals(user, create);
            });
        });
    }

    @Test
    public void testServiceUpdateUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserService.find(1);
                user.username = "Update test";
                User update = UserService.update(user);
                assertEquals(update.username, "Update test");
            });
        });
    }

    @Test
    public void testServiceDeleteUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                long count = UserService.count();
                assertEquals(count, 2);

                assertTrue(UserService.delete(1));

                count = UserService.count();
                assertEquals(count, 1);
            });
        });
    }

    public void testServiceDeleteNotFoundUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                assertFalse(UserService.delete(0));
            });
        });
    }

    @Test
    public void testServiceAddFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User friend = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                friend = UserService.create(friend);

                assertTrue(UserService.addFriend(1, friend.id));
            });
        });
    }

    @Test
    public void testServiceAddFriendNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addFriend(1, 0));
                assertFalse(UserService.addFriend(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddFriendAlreadyFriends() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addFriend(1, 2));
            });
        });
    }

    @Test
    public void testServiceAddFriendSameUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addFriend(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFriend() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(UserService.deleteFriend(1, 2));
            });
        });
    }

    @Test
    public void testServiceDeleteFriendNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.deleteFriend(1, 0));
                assertFalse(UserService.deleteFriend(0, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFriendNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User friend = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                friend = UserService.create(friend);

                assertFalse(UserService.deleteFriend(1, friend.id));
            });
        });
    }

    @Test
    public void testServiceAddFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = UserService.create(user);

                assertTrue(UserService.addFavorite(user.id, 1));
            });
        });
    }

    @Test
    public void testServiceAddFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addFavorite(1, 0));
                assertFalse(UserService.addFavorite(0, 1));
            });
        });
    }

    @Test
    public void testServiceAddFavoriteAlredyFav() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addFavorite(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFavorite() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(UserService.deleteFavorite(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFavoriteNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.deleteFavorite(1, 0));
                assertFalse(UserService.deleteFavorite(0, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteFavoriteNotExist() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.deleteFavorite(2, 1));
            });
        });
    }

    @Test
    public void testServiceAddRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = new User("New test", "email@email.com", "password", null, null, TypeUser.COMUN);
                user = UserService.create(user);

                assertTrue(UserService.addRating(user.id, 1, 4.3));
            });
        });
    }

    @Test
    public void testServiceAddRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addRating(1, 0, 0.0));
                assertFalse(UserService.addRating(0, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceAddRatingAlredyRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addRating(1, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceAddRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.addRating(1, 1, -0.01));
                assertFalse(UserService.addRating(1, 1, 5.01));
            });
        });
    }

    @Test
    public void testServiceUpdateRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(UserService.updateRating(1, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceUpdateRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.updateRating(1, 0, 0.0));
                assertFalse(UserService.updateRating(0, 1, 0.0));
            });
        });
    }

    @Test
    public void testServiceUpdateRatingValueIncorrect() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.updateRating(1, 1, -0.01));
                assertFalse(UserService.updateRating(1, 1, 5.01));
            });
        });
    }

    @Test
    public void testServiceDeleteRating() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertTrue(UserService.deleteRating(1, 1));
            });
        });
    }

    @Test
    public void testServiceDeleteRatingNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();

                assertFalse(UserService.deleteRating(1, 0));
                assertFalse(UserService.deleteRating(0, 1));
            });
        });
    }
}
