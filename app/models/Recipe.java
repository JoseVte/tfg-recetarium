
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import controllers.RecipeController.IngredientRequest;
import controllers.RecipeController.RecipeRequest;
import models.base.Model;
import models.enums.RecipeDifficulty;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;
import models.service.CategoryService;
import models.service.RecipeService;
import models.service.UserService;
import util.serializer.RecipeCommentsSerializer;
import util.serializer.RecipeTagsSerializer;

@Entity
@Table(name = "recipes")
@JsonPropertyOrder({ "id", "slug", "title", "ingredients", "steps", "duration", "num_persons", "difficulty", "user",
        "category", "tags", "comments", "media", "created_at", "updated_at" })
public class Recipe extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    public String             slug;

    @Column(nullable = false)
    public String             title;

    @Column(columnDefinition = "text")
    public String             steps;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Ingredient>   ingredients      = new ArrayList<Ingredient>();

    @Temporal(TemporalType.TIME)
    @Column(nullable = false)
    public Date               duration;

    @JsonProperty(value = "num_persons")
    @Column(nullable = false, name = "num_persons", columnDefinition = "int(5) default '0'")
    public Integer            numPersons       = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public RecipeDifficulty   difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    public User               user;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "category_id", nullable = true)
    public Category           category;

    @JsonSerialize(using = RecipeCommentsSerializer.class)
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment>      comments         = new ArrayList<Comment>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Favorite>     favorites        = new ArrayList<Favorite>();

    @JsonIgnore
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Rating>       ratings          = new ArrayList<Rating>();

    @JsonSerialize(using = RecipeTagsSerializer.class)
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<RecipeTags>   tags             = new ArrayList<RecipeTags>();

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Media>        media            = new ArrayList<Media>();

    public Recipe() {
    }

    public Recipe(String slug, String title, String steps, Date duration, RecipeDifficulty diff, Integer numPersons,
            User user, Category category) {
        this.slug = slug;
        this.title = title;
        this.steps = steps;
        this.duration = duration;
        this.difficulty = diff;
        this.numPersons = numPersons;
        this.user = user;
        this.category = category;
    }

    public Recipe(RecipeRequest recipe) {
        this.id = recipe.id;
        this.slug = recipe.slug;
        this.title = recipe.title;
        this.steps = recipe.steps;
        this.duration = recipe.durationParsed;
        this.difficulty = recipe.difficulty;
        if (recipe.num_persons != null)
            this.numPersons = recipe.num_persons;
        this.user = UserService.findByEmailAddress(recipe.email);
        if (recipe.category_id != null)
            this.category = CategoryService.find(recipe.category_id);
        this.ingredients = new ArrayList<Ingredient>();
        for (IngredientRequest ingredient : recipe.ingredients) {
            this.ingredients.add(new Ingredient(ingredient.name, ingredient.count));
        }
    }

    /*
     * (non-Javadoc)
     * @see models.base.Model#prePersistData()
     */
    @Override
    public void prePersistData() {
        if (steps != null && steps.isEmpty())
            steps = null;
    }

    /*
     * (non-Javadoc)
     * @see models.base.Model#postPersistData()
     */
    @Override
    public void postPersistData() {
        RecipeService.updateIngredients(this);
    }

    /*
     * (non-Javadoc)
     * @see models.base.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        Recipe recipe = (Recipe) old;
        user = recipe.user;
        comments = recipe.comments;
        favorites = recipe.favorites;
        ratings = recipe.ratings;
        tags = recipe.tags;
        media = recipe.media;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Recipe [id=" + id + ", slug=" + slug + ", title=" + title + ", steps=" + steps + ", duration="
                + duration + ", numPersons=" + numPersons + ", difficulty=" + difficulty + ", ingredients="
                + ingredients.size() + ", user=" + user.id + ", section=" + (category != null ? category.text : "")
                + ", comments=" + comments.size() + ", favorites=" + favorites.size() + ", ratings=" + ratings.size()
                + ", tags=" + tags.size() + ", media=" + media.size() + "]";
    }

}
