package models.dao;

import models.Comment;
import models.base.CrudDAO;
import play.db.jpa.JPA;

import java.util.List;

public class CommentDAO extends CrudDAO<Comment> {
    public CommentDAO() {
        super(Comment.class);
    }

    public List<Comment> replies(Integer recipeId, Integer id) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE recipe_id = " + recipeId + " AND parent_comment_id = " + id, Comment.class).getResultList();
    }
}
