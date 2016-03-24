package models.service;

import models.Comment;
import models.dao.CommentDAO;

import java.util.List;

public class CommentService {
    private static final CommentDAO dao;

    static {
        dao = new CommentDAO();
    }

    /**
     * Create a comment
     *
     * @param data Comment
     *
     * @return Comment
     */
    public static Comment create(Comment data) {
        return dao.create(data);
    }

    /**
     * Update a comment
     *
     * @param data Comment
     *
     * @return Comment
     */
    public static Comment update(Comment data) {
        return dao.update(data);
    }

    /**
     * Find a comment by id
     *
     * @param id Integer
     *
     * @return Comment
     */
    public static Comment find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete a comment by id
     *
     * @param id Integer
     */
    public static Boolean delete(Integer id) {
        Comment comment = dao.find(id);
        return delete(comment);
    }

    /**
     * Delete a comment by model
     *
     * @param comment Comment
     */
    public static Boolean delete(Comment comment) {
        if (comment != null) {
            dao.delete(comment);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all comments
     *
     * @return List<Comment>
     */
    public static List<Comment> all() {
        return dao.all();
    }

    /**
     * Get the page of comments
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<Comment>
     */
    public static List<Comment> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Get the number of total of comments
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }

    public static List<Comment> getReplies(Integer recipeId, Integer id) {
        return dao.replies(recipeId, id);
    }
}
