package models.dao;

import java.util.List;

import models.Comment;
import play.db.jpa.JPA;

public class CommentDAO {
    static String TABLE = Comment.class.getName();

    /**
     * Create a comment
     *
     * @param Comment model
     *
     * @return Comment
     */
    public static Comment create(Comment model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find a comment by id
     *
     * @param Integer id
     *
     * @return Comment
     */
    public static Comment find(Integer id) {
        return JPA.em().find(Comment.class, id);
    }

    /**
     * Update a comment
     *
     * @param Comment model
     *
     * @return Comment
     */
    public static Comment update(Comment model) {
        Comment aux = JPA.em().getReference(Comment.class, model.id);
        model.setCreatedAt(aux.getCreatedAt());
        return JPA.em().merge(model);
    }

    /**
     * Delete a comment by id
     *
     * @param Comment comment
     */
    public static void delete(Comment comment) {
        JPA.em().remove(comment);
    }

    /**
     * Get all comments
     *
     * @return List<Comment>
     */
    @SuppressWarnings("unchecked")
    public static List<Comment> all() {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of comments
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Comment>
     */
    @SuppressWarnings("unchecked")
    public static List<Comment> paginate(Integer page, Integer size) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page * size)
                .setMaxResults(size).getResultList();
    }

    /**
     * Get the number of total row
     *
     * @return Long
     */
    public static Long count() {
        return (Long) JPA.em().createQuery("SELECT count(m) FROM " + TABLE + " m").getSingleResult();
    }
}
