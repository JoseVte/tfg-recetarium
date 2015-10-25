
package models;
import util.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners({
        CreatedAtListener.class,
        UpdatedAtListener.class
})
@Table(name = "recipes")
public class Recipe implements Creatable, Updatable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

    @Column(unique=true, nullable=false)
    public String slug;

    @Column(nullable = false)
    public String title;
    public String description;
    
    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id")
    public User user;
    
    @OneToMany(mappedBy="recipe")
    public List<Comment> comments;
    
    @ManyToMany(mappedBy="recipesFavorites")
    public List<User> favorites;
    
    @OneToMany(mappedBy="recipe",fetch=FetchType.LAZY)
    public List<Rating> ratings;
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="recipe_tags",
    	joinColumns={@JoinColumn(name="recipe_id")},
    	inverseJoinColumns={@JoinColumn(name="tag_id")},
    	uniqueConstraints={@UniqueConstraint(columnNames={"tag_id","recipe_id"})})
    public List<Tag> tags;

    @Column(name="created_at", insertable=false, nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at", insertable=false, nullable=false)
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
    
    public Recipe() {}

	public Recipe(String slug, String title, String description, User user, Date createdAt, Date updatedAt) {
		this.slug = slug;
		this.title = title;
		this.description = description;
		this.user = user;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
}