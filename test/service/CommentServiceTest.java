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

import models.Comment;
import models.service.CommentService;
import models.service.RecipeService;
import models.service.UserService;
import play.db.jpa.JPA;
import util.AbstractTest;

public class CommentServiceTest extends AbstractTest {

    @Test
    public void testCommentServiceFindComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentService.find(1);
                assertEquals(comment.text, "test");
                assertEquals(comment.user.id.intValue(), 1);
                assertEquals(comment.recipe.id.intValue(), 1);
                assertEquals(comment.replies.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testCommentServiceNotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentService.find(0);
                assertNull(comment);

                successTest();
            });
        });
    }

    @Test
    public void testCommentServiceFindAllComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Comment> comments = CommentService.all();
                long count = CommentService.count();
                assertEquals(count, 2);

                assertEquals(comments.get(0).text, "test");

                successTest();
            });
        });
    }

    @Test
    public void testCommentServicePageComments() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                List<Comment> comments = CommentService.paginate(0, 1);
                assertEquals(comments.get(0).text, "test");
                assertEquals(comments.size(), 1);

                comments = CommentService.paginate(1, 1);
                assertEquals(comments.size(), 1);

                successTest();
            });
        });
    }

    @Test
    public void testCommentServiceCreateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment create = new Comment("test2", UserService.find(1), RecipeService.find(1), null);
                Comment comment = CommentService.create(create);
                assertEquals(comment, create);

                successTest();
            });
        });
    }

    @Test
    public void testCommentServiceUpdateComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                Comment comment = CommentService.find(1);
                comment.text = "Update test";
                Comment update = CommentService.update(comment);
                assertEquals(update.text, "Update test");

                successTest();
            });
        });
    }

    @Test
    public void testCommentServiceDeleteComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                long count = CommentService.count();
                assertEquals(count, 2);

                assertTrue(CommentService.delete(1));

                count = CommentService.count();
                assertEquals(count, 0);

                successTest();
            });
        });
    }

    @Test
    public void testCommentServiceDeleteNotFoundComment() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeDataModel();
                assertFalse(CommentService.delete(0));

                successTest();
            });
        });
    }
}
