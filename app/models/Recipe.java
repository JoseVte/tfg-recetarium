
package models;

import util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.dao.RecipeDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "recipe")
    public List<Comment>      comments;

    @ManyToMany(mappedBy = "recipesFavorites")
    public List<User>         favorites;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    public List<Rating>       ratings;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "recipe_tags", joinColumns = { @JoinColumn(name = "recipe_id") }, inverseJoinColumns = {
            @JoinColumn(name = "tag_id") }, uniqueConstraints = {
                    @UniqueConstraint(columnNames = { "tag_id", "recipe_id" }) })
    public List<Tag>          tags;

    @OneToMany(mappedBy = "recipe")
    public List<Media>        media;

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

    public void emptyToNull() {
        if (description != null && description.isEmpty()) description = null;
    }
}
