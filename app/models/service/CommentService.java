package models.service;

import java.util.List;

import models.Comment;
import models.dao.CommentDAO;

public class CommentService {
    private static CommentDAO dao;

    static {
        dao = new CommentDAO();
    }

    /**
     * Create a comment
     *
     * @param Comment data
     *
     * @return Comment
     */
    public static Comment create(Comment data) {
        return dao.create(data);
    }

    /**
     * Update a comment
     *
     * @param Comment data
     *
     * @return Comment
     */
    public static Comment update(Comment data) {
        return dao.update(data);
    }

    /**
     * Find a comment by id
     *
     * @param Integer id
     *
     * @return Comment
     */
    public static Comment find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete a comment by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Comment comment = dao.find(id);
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
     * @param Integer page
     * @param Integer size
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
}
