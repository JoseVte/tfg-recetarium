package models.dao;

import java.util.List;

import models.Recipe;
import models.Category;
import models.Tag;
import models.User;
import models.base.CrudDAO;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;
import play.db.jpa.JPA;

public class RecipeDAO extends CrudDAO<Recipe> {
    public RecipeDAO() {
        super(Recipe.class);
    }
    
    
    public Recipe findBySlug(String slug) {
        List<Recipe> result = JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE slug = '" + slug + "'", Recipe.class).getResultList();
        if (!result.isEmpty()) return result.get(0);
        return null;
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
    public List<Recipe> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id", Recipe.class).getResultList();
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
    public List<Recipe> check(String field, Object value, Integer id) {
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
        RecipeTags tagged = JPA.em().createQuery("SELECT m FROM " + RecipeTags.class.getName()
                + " m WHERE tag_id = " + tag.id + " AND recipe_id = " + recipe.id, RecipeTags.class).getSingleResult();
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
        Favorite fav = JPA.em().createQuery("SELECT m FROM " + Favorite.class.getName()
                + " m WHERE user_id = " + user.id + " AND recipe_id = " + recipe.id, Favorite.class).getSingleResult();
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
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
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
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = "
                + user.id + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
        JPA.em().remove(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Add section to a recipe
     *
     * @param category
     * @param recipe
     */
    public static void addOrUpdateCategory(Category category, Recipe recipe) {
        Category oldCategory = recipe.category;
        recipe.category = category;
        JPA.em().merge(recipe);
        // Reload entities
        JPA.em().flush();
        if (oldCategory != null) JPA.em().refresh(oldCategory);
        JPA.em().refresh(category);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete the section of a recipe
     *
     * @param recipe
     */
    public static void deleteCategory(Recipe recipe) {
        Category category = recipe.category;
        recipe.category = null;
        JPA.em().merge(recipe);
        // Reload entities
        JPA.em().flush();
        if (category != null) JPA.em().refresh(category);
        JPA.em().refresh(recipe);
    }
}
