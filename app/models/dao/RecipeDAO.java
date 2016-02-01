package models.dao;

import models.*;
import models.base.CrudDAO;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;
import models.service.UserService;
import play.db.jpa.JPA;

import javax.persistence.NoResultException;
import java.util.List;

public class RecipeDAO extends CrudDAO<Recipe> {
    public RecipeDAO() {
        super(Recipe.class);
    }

    /**
     * Add a tag to a recipe
     *
     * @param tag    Tag
     * @param recipe Recipe
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
     * Add all tags into a recipe
     *
     * @param tags   List<Tag>
     * @param recipe Recipe
     */
    public static void addTags(List<Tag> tags, Recipe recipe) {
        for (Tag tag : tags) {
            RecipeTags model = new RecipeTags(tag, recipe);
            JPA.em().persist(model);
            tag.recipes.add(model);
        }
        JPA.em().flush();
        JPA.em().refresh(recipe);
    }

    /**
     * Delete a tag of a recipe
     *
     * @param tag    Tag
     * @param recipe Recipe
     */
    public static void deleteTag(Tag tag, Recipe recipe) {
        RecipeTags tagged = JPA.em().createQuery("SELECT m FROM " + RecipeTags.class.getName() + " m WHERE tag_id = " + tag.id + " AND recipe_id = " + recipe.id, RecipeTags.class).getSingleResult();
        JPA.em().remove(tagged);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(tag);
        JPA.em().refresh(recipe);
    }

    /**
     * Delete all tag of a recipe
     *
     * @param recipe Recipe
     */
    public static void deleteTags(Recipe recipe) {
        JPA.em().createQuery("DELETE FROM " + RecipeTags.class.getName() + " WHERE recipe_id = " + recipe.id).executeUpdate();
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(recipe);
    }

    /**
     * Add a recipe as a favorite
     *
     * @param user   User
     * @param recipe Recipe
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
     * @param user   User
     * @param recipe Recipe
     */
    public static void deleteFavorite(User user, Recipe recipe) {
        Favorite fav = JPA.em().createQuery("SELECT m FROM " + Favorite.class.getName() + " m WHERE user_id = " + user.id + " AND recipe_id = " + recipe.id, Favorite.class).getSingleResult();
        JPA.em().remove(fav);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Add a rating of a recipe
     *
     * @param user   User
     * @param recipe Recipe
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
     * @param user   User
     * @param recipe Recipe
     */
    public static void updateRating(User user, Recipe recipe, double value) {
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = " + user.id + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
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
     * @param user   User
     * @param recipe Recipe
     */
    public static void deleteRating(User user, Recipe recipe) {
        Rating rating = JPA.em().createQuery("SELECT m FROM " + Rating.class.getName() + " m WHERE user_id = " + user.id + " AND recipe_id = " + recipe.id, Rating.class).getSingleResult();
        JPA.em().remove(rating);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(user);
        JPA.em().refresh(recipe);
    }

    /**
     * Add section to a recipe
     *
     * @param category Category
     * @param recipe   Recipe
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
     * @param recipe Recipe
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

    /**
     * Get the page of models
     *
     * @param search String
     * @param page   Integer
     * @param size   Integer
     * @param user   String
     *
     * @return List<Recipe>
     */
    public List<Recipe> paginate(Integer page, Integer size, String search, String user) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE title LIKE '%" + search + "%' OR steps LIKE '%" + search + "%' AND " + Recipe.IsVisible(user) + " ORDER BY id", Recipe.class).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    /**
     * Count the all events with search parameter
     *
     * @param search String
     * @param user   String
     *
     * @return Long
     */
    public Long count(String search, String user) {
        return JPA.em().createQuery("SELECT count(m) FROM " + TABLE + " m WHERE title LIKE '%" + search + "%' OR steps LIKE '%" + search + "%' AND " + Recipe.IsVisible(user), Long.class).getSingleResult();
    }

    /**
     * Find a recipe by the slug
     *
     * @param slug String
     *
     * @return List<Recipe>
     */
    public Recipe findBySlug(String slug) {
        List<Recipe> result = JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE slug = '" + slug + "'", Recipe.class).getResultList();
        if (!result.isEmpty()) return result.get(0);
        return null;
    }

    /**
     * Find a recipe by the slug and id
     *
     * @param slug String
     *
     * @return Recipe
     */
    public Recipe findBySlugAndId(String slug, Integer id) {
        try {
            return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE slug = '" + slug + "' AND id != " + id, Recipe.class).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Find a recipe if the email is from the user creator or an admin
     *
     * @param email    String
     * @param idRecipe IntegerRecipe
     *
     * @return Recipe
     */
    public Recipe findByOwner(String email, Integer idRecipe) {
        try {
            User logged = UserService.findByEmailAddress(email);
            if (logged != null) {
                if (logged.isAdmin()) {
                    return find(idRecipe);
                }
                return JPA.em().createQuery("SELECT m FROM " + TABLE + " m JOIN m.user u WHERE m.id = '" + idRecipe + "' AND u.id = '" + logged.id + "'", Recipe.class).getSingleResult();
            }
            return null;
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Where clause
     *
     * @param field      String
     * @param value      Object
     * @param id         Integer
     * @param comparison String
     *
     * @return List<Recipe>
     */
    public List<Recipe> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " " + comparison + " '" + value + "' ORDER BY id", Recipe.class).getResultList();
    }

    /**
     * Where clause
     *
     * @param field String
     * @param value Object
     * @param id    Integer
     *
     * @return List<Recipe>
     */
    public List<Recipe> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }

    /**
     * Add all ingredients for a recipe
     *
     * @param recipe Recipe
     */
    public void addIngredients(Recipe recipe) {
        for (Ingredient ingredient : recipe.ingredients) {
            ingredient.recipe = recipe;
            JPA.em().persist(ingredient);
        }
        JPA.em().flush();
        JPA.em().refresh(recipe);
    }

    /**
     * Delete all ingredients for a recipe
     *
     * @param recipe Recipe
     */
    public void deleteIngredients(Recipe recipe) {
        JPA.em().createQuery("DELETE FROM " + Ingredient.class.getName() + " WHERE recipe_id = " + recipe.id).executeUpdate();
    }
}
