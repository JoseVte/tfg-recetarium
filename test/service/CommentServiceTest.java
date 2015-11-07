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

import models.Comment;
import models.service.CommentService;
import models.service.RecipeService;
import models.service.UserService;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class CommentServiceTest extends WithApplication {

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
    public void testServiceFindComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentService.find(1);
                assertEquals(comment.text, "test");
                assertEquals(comment.user.id.intValue(), 1);
                assertEquals(comment.recipe.id.intValue(), 1);
                assertEquals(comment.replies.size(), 1);
            });
        });
    }

    @Test
    public void testServiceNotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentService.find(0);
                assertNull(comment);
            });
        });
    }

    @Test
    public void testServiceFindAllComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Comment> comments = CommentService.all();
                long count = CommentService.count();
                assertEquals(count, 2);

                assertEquals(comments.get(0).text, "test");
            });
        });
    }

    @Test
    public void testServicePageComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Comment> comments = CommentService.paginate(0, 1);
                assertEquals(comments.get(0).text, "test");
                assertEquals(comments.size(), 1);

                comments = CommentService.paginate(1, 1);
                assertEquals(comments.size(), 1);
            });
        });
    }

    @Test
    public void testServiceCreateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment create = new Comment("test2", UserService.find(1), RecipeService.find(1), null);
                Comment comment = CommentService.create(create);
                assertEquals(comment, create);
            });
        });
    }

    @Test
    public void testServiceUpdateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentService.find(1);
                comment.text = "Update test";
                Comment update = CommentService.update(comment);
                assertEquals(update.text, "Update test");
            });
        });
    }

    @Test
    public void testServiceDeleteComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                long count = CommentService.count();
                assertEquals(count, 2);

                assertTrue(CommentService.delete(1));

                count = CommentService.count();
                assertEquals(count, 0);
            });
        });
    }

    public void testServiceDeleteNotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                assertFalse(CommentService.delete(0));
            });
        });
    }
}
