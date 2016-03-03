package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import controllers.RecipeController.RecipeRequest;
import models.base.Model;
import models.enums.RecipeDifficulty;
import models.enums.RecipeVisibility;
import models.manytomany.*;
import models.service.CategoryService;
import models.service.UserService;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import play.libs.Json;
import util.serializer.RecipeCommentsSerializer;
import util.serializer.RecipeFilesSerializer;
import util.serializer.RecipeFavoritesSerializer;
import util.serializer.RecipeRatingSerializer;
import util.serializer.RecipeTagsSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "recipes")
@JsonPropertyOrder({"id", "slug", "title", "ingredients", "steps", "duration", "num_persons", "difficulty", "visibility", "is_draft", "user",
        "category", "tags", "comments", "favorites", "rating", "files", "created_at", "updated_at"})
public class Recipe extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(unique = true, nullable = false)
    public String slug;

    @Column(nullable = false)
    public String title;

    @Column(columnDefinition = "text")
    public String steps;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Ingredient> ingredients = new ArrayList<Ingredient>();

    @Temporal(TemporalType.TIME)
    @Column(nullable = false)
    public Date duration;

    @JsonProperty(value = "num_persons")
    @Column(nullable = false, name = "num_persons", columnDefinition = "int(5) default '0'")
    public Integer numPersons = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public RecipeDifficulty difficulty = RecipeDifficulty.EASY;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public RecipeVisibility visibility = RecipeVisibility.PUBLIC;

    @JsonProperty(value = "is_draft")
    @Column(nullable = false, name = "is_draft", columnDefinition = "boolean default false")
    public Boolean isDraft = false;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "category_id", nullable = true)
    public Category category;

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = RecipeCommentsSerializer.class)
    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Comment> comments = new ArrayList<Comment>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = RecipeFavoritesSerializer.class)
    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Favorite> favorites = new ArrayList<Favorite>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = RecipeRatingSerializer.class)
    @JsonProperty(value = "rating")
    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Rating> ratings = new ArrayList<Rating>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = RecipeTagsSerializer.class)
    @OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<RecipeTags> tags = new ArrayList<RecipeTags>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = RecipeFilesSerializer.class)
    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<RecipeFiles> files = new ArrayList<RecipeFiles>();

    public Recipe() {
    }

    public Recipe(String slug, String title, String steps, Date duration, RecipeDifficulty diff, Integer numPersons, User user, Category category, RecipeVisibility visibility) {
        this.slug = slug;
        this.title = title;
        this.steps = steps;
        this.duration = duration;
        this.difficulty = diff;
        this.visibility = visibility;
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
        this.visibility = recipe.visibility;
        this.isDraft = recipe.is_draft;
        if (recipe.num_persons != null) this.numPersons = recipe.num_persons;
        this.user = UserService.findByEmailAddress(recipe.email);
        if (recipe.category_id != null) this.category = CategoryService.find(recipe.category_id);
    }

    @JsonIgnore
    public static String Search(String search) {
        return "(title LIKE '%" + search + "%' OR steps LIKE '%" + search + "%')";
    }

    @JsonIgnore
    public static String IsVisible(String user) {
        String query = "(m.visibility = '" + RecipeVisibility.PUBLIC + "'";
        if (user != null && !user.equals("anonymous")) {
            User userParsed = Json.fromJson(Json.parse(user), User.class);
            if (!userParsed.isAdmin()) {
                String subquery = "(SELECT u.friend FROM " + Friend.class.getName() + " u WHERE u.user = " + userParsed.id + ")";
                query += " OR (m.visibility = '" + RecipeVisibility.PRIVATE + "' AND m.user = " + userParsed.id + ") OR (m.visibility = '" + RecipeVisibility.FRIENDS + "' AND m.user IN " + subquery + ")";
            } else {
                query += "OR m.visibility = '" + RecipeVisibility.PRIVATE + "' OR m.visibility = '" + RecipeVisibility.FRIENDS + "'";
            }
        }
        return query + ")";
    }

    @JsonIgnore
    public static String WithDrafts(Boolean with) {
        return "(m.isDraft = " + with + ")";
    }

    /*
     * (non-Javadoc)
     *
     * @see models.base.Model#prePersistData()
     */
    @Override
    public void prePersistData() {
        if (steps != null && steps.isEmpty()) steps = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see models.base.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        Recipe recipe = (Recipe) old;
        ingredients = recipe.ingredients;
        user = recipe.user;
        comments = recipe.comments;
        favorites = recipe.favorites;
        ratings = recipe.ratings;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Recipe [id=" + id + ", slug=" + slug + ", title=" + title + ", steps=" + steps + ", duration="
                + duration + ", numPersons=" + numPersons + ", difficulty=" + difficulty + ", visibility=" + visibility
                + ", ingredients=" + ingredients.size() + ", user=" + user.id
                + ", section=" + (category != null ? category.text : "") + ", comments=" + comments.size() + ", favorites=" + favorites.size()
                + ", ratings=" + ratings.size() + ", tags=" + tags.size() + ", files=" + files.size() + "]";
    }

    @JsonIgnore
    public boolean isVisible(String user) {
        if (user != null && !user.equals("anonymous")) {
            User userParsed = Json.fromJson(Json.parse(user), User.class);
            if (visibility.equals(RecipeVisibility.FRIENDS) && (this.user.friends.contains(userParsed) || userParsed.isAdmin()))
                return true;
            if (visibility.equals(RecipeVisibility.PRIVATE) && (Objects.equals(this.user.id, userParsed.id)) || userParsed.isAdmin())
                return true;
        }
        return visibility.equals(RecipeVisibility.PUBLIC);
    }
}
