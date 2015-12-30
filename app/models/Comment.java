package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.base.Model;
import models.dao.CommentDAO;
import play.data.validation.Constraints;

@Entity
@Table(name = "comments")
@JsonPropertyOrder({ "id", "text", "recipe", "user", "parent", "created_at", "updated_at" })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Comment extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    @Column(nullable = false)
    public String             text;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    public Recipe             recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User               user;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    public Comment            parent;

    //@JsonIgnore
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment>      replies;

    public Comment() {
        dao = new CommentDAO();
    }

    public Comment(String text, User user, Recipe recipe, Comment parent) {
        dao = new CommentDAO();
        this.text = text;
        this.user = user;
        this.recipe = recipe;
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#prePersistData()
     */
    @Override
    public void prePersistData() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        Comment comment = (Comment) old;
        this.setCreatedAt(comment.getCreatedAt());
        this.replies = comment.replies;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Comment [id=" + id + ", text=" + text + ", recipe=" + recipe.id + ", user=" + user.id + ", parent="
                + (parent != null ? parent.id : "false") + ", replies=" + replies.size() + "]";
    }
}
