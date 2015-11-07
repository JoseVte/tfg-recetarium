package models.dao;

import java.util.List;

import models.Recipe;
import models.Tag;
import models.User;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;
import play.db.jpa.JPA;

public class RecipeDAO {
    static String TABLE = Recipe.class.getName();

    /**
     * Create an recipe
     *
     * @param Recipe model
     *
     * @return Recipe
     */
    public static Recipe create(Recipe model) {
        model.prePersistData();
        JPA.em().persist(model);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return model;
    }

    /**
     * Find an recipe by id
     *
     * @param Integer id
     *
     * @return Recipe
     */
    public static Recipe find(Integer id) {
        return JPA.em().find(Recipe.class, id);
    }

    /**
     * Update an recipe
     *
     * @param Recipe model
     *
     * @return Recipe
     */
    public static Recipe update(Recipe model) {
        return JPA.em().merge(model);
    }

    /**
     * Delete an recipe by id
     *
     * @param Recipe model
     */
    public static void delete(Recipe model) {
        JPA.em().remove(model);
    }

    /**
     * Get all recipes
     *
     * @return List<Recipe>
     */
    @SuppressWarnings("unchecked")
    public static List<Recipe> all() {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of recipes
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Recipe>
     */
    @SuppressWarnings("unchecked")
    public static List<Recipe> paginate(Integer page, Integer size) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page * size)
                .setMaxResults(size).getResultList();
    }

    /**
     * Get the number of total row
     *
     * @return Long
     */
    public static Long count() {
        return (Long) JPA.em().createQuery("SELECT count(m) FROM " + TABLE + " m").getSingleResult();
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     * @param String comparison
     *
     * @return List<Recipe>
     */
    @SuppressWarnings("unchecked")
    public static List<Recipe> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id").getResultList();
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     *
     * @return List<Recipe>
     */
    public static List<Recipe> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }

    /**
     * Add a tag to a recipe
     *
     * @param tag
     * @param recipe
     */
    public static void addTag(Tag tag, Recipe recipe) {
        RecipeTags tagged = new RecipeTags(tag, recipe);
        JPA.em().persist(tagged);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(tag);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete a tag of a recipe
     *
     * @param tag
     * @param recipe
     */
    public static void deleteTag(Tag tag, Recipe recipe) {
        RecipeTags tagged = (RecipeTags) JPA.em().createQuery("SELECT m FROM " + RecipeTags.class.getName()
                + " m WHERE tag_id = " + tag.id + " AND recipe_id = " + recipe.id).getSingleResult();
        JPA.em().remove(tagged);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(tag);
        JPA.em().refresh(recipe);
    }

    /**
     * Add a recipe as a favorite
     *
     * @param user
     * @param recipe
     */
    public static void addFavorite(User user, Recipe recipe) {
        Favorite fav = new Favorite(user, recipe);
        JPA.em().persist(fav);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete a recipe favorite
     *
     * @param user
     * @param recipe
     */
    public static void deleteFavorite(User user, Recipe recipe) {
        Favorite fav = (Favorite) JPA.em().createQuery("SELECT m FROM " + Favorite.class.getName()
                + " m WHERE user_id = " + user.id + " AND recipe_id = " + recipe.id).getSingleResult();
        JPA.em().remove(fav);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Add a rating of a recipe
     *
     * @param user
     * @param recipe
     */
    public static void addRating(User user, Recipe recipe, double value) {
        Rating rating = new Rating(user, recipe, value);
        JPA.em().persist(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Update a rating of a recipe
     *
     * @param user
     * @param recipe
     */
    public static void updateRating(User user, Recipe recipe, double value) {
        Rating rating = (Rating) JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id).getSingleResult();
        rating.rating = value;
        JPA.em().merge(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete a rating of a recipe
     *
     * @param user
     * @param recipe
     */
    public static void deleteRating(User user, Recipe recipe) {
        Rating rating = (Rating) JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id).getSingleResult();
        JPA.em().remove(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }
}
