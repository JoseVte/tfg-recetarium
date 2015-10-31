package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import play.data.validation.Constraints;
import util.*;

@Entity
@EntityListeners({
        CreatedAtListener.class,
        UpdatedAtListener.class
})
@Table(name = "comments")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comment implements Creatable, Updatable {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

	@Constraints.Required
	@Column(nullable = false)
    public String text;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id")
    public Recipe recipe;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    public User user;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="parent_comment_id")
    public Comment parent;
    
    @OneToMany(mappedBy="parent")
    public List<Comment> replies;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Override
    public void setCreatedAt(Date date) {
        this.createdAt = date;
    }

    @Override
    public void setUpdatedAt(Date date) {
        this.updatedAt = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public Comment () {}
    
    public Comment(String text) {
    	this.text = text;
    }
}
