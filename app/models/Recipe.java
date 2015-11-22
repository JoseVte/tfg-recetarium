
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import models.dao.RecipeDAO;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Model;

@Entity
@Table(name = "recipes")
@JsonPropertyOrder({ "id", "slug", "title", "description", "user", "category", "created_at", "updated_at" })
public class Recipe extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    @Column(unique = true, nullable = false)
    public String             slug;

    @Constraints.Required
    @Column(nullable = false)
    public String             title;
    public String             description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    public User               user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id")
    public Category           category;

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment>      comments         = new ArrayList<Comment>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Favorite>     favorites        = new ArrayList<Favorite>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Rating>       ratings          = new ArrayList<Rating>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<RecipeTags>   tags             = new ArrayList<RecipeTags>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Media>        media            = new ArrayList<Media>();

    public Recipe() {
        dao = new RecipeDAO();
    }

    public Recipe(String slug, String title, String description, User user) {
        dao = new RecipeDAO();
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.user = user;
    }

    public Recipe(String slug, String title, String description, User user, Category category) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.user = user;
        this.category = category;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!((RecipeDAO) dao).check("slug", slug, id).isEmpty()) {
            errors.add(new ValidationError("slug", "This slug is already used."));
        }
        return errors.isEmpty() ? null : errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#prePersistData()
     */
    @Override
    public void prePersistData() {
        if (description != null && description.isEmpty()) description = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        Recipe recipe = (Recipe) old;
        comments = recipe.comments;
        favorites = recipe.favorites;
        ratings = recipe.ratings;
        tags = recipe.tags;
        media = recipe.media;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Recipe [id=" + id + ", slug=" + slug + ", title=" + title + ", description=" + description + ", user="
                + user.id + ", section=" + (category != null ? category.text : "") + ", comments=" + comments.size()
                + ", favorites=" + favorites.size() + ", ratings=" + ratings.size() + ", tags=" + tags.size()
                + ", media=" + media.size() + "]";
    }

}
