package dao;

import models.Comment;
import org.junit.Test;
import play.db.jpa.JPA;
import util.AbstractTest;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static play.test.Helpers.*;

public class CommentModelDAOTest extends AbstractTest {

    @Test
    public void testCommentDAOFindComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = commentDAO.find(1);
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
                Comment comment = commentDAO.find(0);
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
                List<Comment> comments = commentDAO.all();
                long count = commentDAO.count();
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
                List<Comment> comments = commentDAO.paginate(0, 1);
                assertEquals(comments.get(0).text, "test");
                assertEquals(comments.size(), 1);

                comments = commentDAO.paginate(1, 1);
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
                Comment create = new Comment("test2", userDAO.find(1), recipeDAO.find(1), null);
                Comment comment = commentDAO.create(create);
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
                Comment comment = commentDAO.find(1);
                comment.text = "Update test";
                Comment update = commentDAO.update(comment);
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
                Comment comment = commentDAO.find(1);
                long count = commentDAO.count();
                assertEquals(count, 2);

                commentDAO.delete(comment);

                count = commentDAO.count();
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
                Comment comment = commentDAO.find(0);

                try {
                    commentDAO.delete(comment);
                } catch (Exception e) {
                }

                successTest();
            });
        });
    }
}
