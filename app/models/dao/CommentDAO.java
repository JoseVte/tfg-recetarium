package models.dao;

import models.Comment;
import models.base.CrudDAO;

public class CommentDAO extends CrudDAO<Comment> {
    public CommentDAO() {
        super(Comment.class);
    }
}
