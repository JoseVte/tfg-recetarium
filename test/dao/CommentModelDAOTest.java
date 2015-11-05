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

import models.Comment;
import models.dao.CommentDAO;
import models.dao.RecipeDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class CommentModelDAOTest extends WithApplication {

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
    public void testDAOFindComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentDAO.find(1);
                assertEquals(comment.text, "test");
                assertEquals(comment.user.id.intValue(), 1);
                assertEquals(comment.recipe.id.intValue(), 1);
                assertNull(comment.parent);
            });
        });
    }

    @Test
    public void testDAONotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentDAO.find(0);
                assertNull(comment);
            });
        });
    }

    @Test
    public void testDAOFindAllComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Comment> comments = CommentDAO.all();
                long count = CommentDAO.count();
                assertEquals(count, 2);

                assertEquals(comments.get(0).text, "test");
            });
        });
    }

    @Test
    public void testDAOPageComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Comment> comments = CommentDAO.paginate(0, 1);
                assertEquals(comments.get(0).text, "test");
                assertEquals(comments.size(), 1);

                comments = CommentDAO.paginate(1, 1);
                assertEquals(comments.size(), 1);
            });
        });
    }

    @Test
    public void testDAOCreateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment create = new Comment("test2", UserDAO.find(1), RecipeDAO.find(1), null);
                Comment comment = CommentDAO.create(create);
                assertEquals(comment, create);
            });
        });
    }

    @Test
    public void testDAOUpdateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentDAO.find(1);
                comment.text = "Update test";
                Comment update = CommentDAO.update(comment);
                assertEquals(update.text, "Update test");
            });
        });
    }

    @Test
    public void testDAODeleteComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentDAO.find(1);
                long count = CommentDAO.count();
                assertEquals(count, 2);

                CommentDAO.delete(comment);

                count = CommentDAO.count();
                assertEquals(count, 0);
            });
        });
    }

    @Test(expected = RuntimeException.class)
    public void testDAODeleteNotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Comment comment = CommentDAO.find(0);

                CommentDAO.delete(comment);
            });
        });
    }
}
