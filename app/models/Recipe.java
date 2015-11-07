
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.dao.RecipeDAO;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Timestamp;
import util.TimestampListener;

@Entity
@EntityListeners({ TimestampListener.class })
@Table(name = "recipes")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Recipe extends Timestamp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

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
    @JoinColumn(name = "section_id")
    public Section            section;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment>      comments         = new ArrayList<Comment>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Favorite>     favorites        = new ArrayList<Favorite>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Rating>       ratings          = new ArrayList<Rating>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<RecipeTags>   tags             = new ArrayList<RecipeTags>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Media>        media            = new ArrayList<Media>();

    public Recipe() {
    }

    public Recipe(String slug, String title, String description, User user) {
        this.slug = slug;
        this.title = title;
        this.description = description;
        this.user = user;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!RecipeDAO.check("slug", slug, id).isEmpty()) {
            errors.add(new ValidationError("slug", "This slug is already used."));
        }
        return errors.isEmpty() ? null : errors;
    }

    public void prePersistData() {
        if (description != null && description.isEmpty()) description = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Recipe [id=" + id + ", slug=" + slug + ", title=" + title + ", description=" + description + ", user="
                + user.id + ", section=" + section.text + ", comments=" + comments.size() + ", favorites="
                + favorites.size() + ", ratings=" + ratings.size() + ", tags=" + tags.size() + ", media=" + media.size()
                + "]";
    }

}
