package models.dao;

import models.Comment;

public class CommentDAO extends CrudDAO<Comment> {
    public CommentDAO() {
        super(Comment.class);
    }
}
