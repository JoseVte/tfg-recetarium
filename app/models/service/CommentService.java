package models.service;

import java.util.List;

import models.Comment;
import models.dao.CommentDAO;

public class CommentService {
    /**
     * Create a comment
     *
     * @param Comment data
     *
     * @return Comment
     */
    public static Comment create(Comment data) {
        return CommentDAO.create(data);
    }

    /**
     * Update a comment
     *
     * @param Comment data
     *
     * @return Comment
     */
    public static Comment update(Comment data) {
        return CommentDAO.update(data);
    }

    /**
     * Find a comment by id
     *
     * @param Integer id
     *
     * @return Comment
     */
    public static Comment find(Integer id) {
        return CommentDAO.find(id);
    }

    /**
     * Delete a comment by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Comment comment = CommentDAO.find(id);
        if (comment != null) {
            CommentDAO.delete(comment);
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
        return CommentDAO.all();
    }

    /**
     * Get the page of comments
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Comment>
     */
    public static List<Comment> paginate(Integer page, Integer size) {
        return CommentDAO.paginate(page, size);
    }

    /**
     * Get the number of total of comments
     *
     * @return Long
     */
    public static Long count() {
        return CommentDAO.count();
    }
}
