package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.util.List;

import org.junit.Test;

import models.Comment;
import models.dao.CommentDAO;
import models.dao.RecipeDAO;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import util.AbstractTest;

public class CommentModelDAOTest extends AbstractTest {

    @Test
    public void testCommentDAOFindComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentDAO.find(1);
                assertEquals(comment.text, "test");
                assertEquals(comment.user.id.intValue(), 1);
                assertEquals(comment.recipe.id.intValue(), 1);
                assertEquals(comment.replies.size(), 1);
                assertNull(comment.parent);
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAONotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentDAO.find(0);
                assertNull(comment);
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAOFindAllComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Comment> comments = CommentDAO.all();
                long count = CommentDAO.count();
                assertEquals(count, 2);

                assertEquals(comments.get(0).text, "test");
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAOPageComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Comment> comments = CommentDAO.paginate(0, 1);
                assertEquals(comments.get(0).text, "test");
                assertEquals(comments.size(), 1);

                comments = CommentDAO.paginate(1, 1);
                assertEquals(comments.size(), 1);
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAOCreateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment create = new Comment("test2", UserDAO.find(1), RecipeDAO.find(1), null);
                Comment comment = CommentDAO.create(create);
                assertEquals(comment, create);
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAOUpdateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentDAO.find(1);
                comment.text = "Update test";
                Comment update = CommentDAO.update(comment);
                assertEquals(update.text, "Update test");
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAODeleteComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentDAO.find(1);
                long count = CommentDAO.count();
                assertEquals(count, 2);

                CommentDAO.delete(comment);

                count = CommentDAO.count();
                assertEquals(count, 0);
            
                successTest();
            });
        });
    }

    @Test
    public void testCommentDAODeleteNotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentDAO.find(0);

                try {
                    CommentDAO.delete(comment);
                } catch (Exception e) {}
            
                successTest();
            });
        });
    }
}
