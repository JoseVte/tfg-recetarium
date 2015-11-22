package models.service;

import java.util.List;

import models.Recipe;
import models.Category;
import models.Tag;
import models.User;
import models.dao.RecipeDAO;
import models.service.UserService;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeTags;

public class RecipeService {
    private static RecipeDAO recipeDAO;
    static {
        recipeDAO = new RecipeDAO();
    }

    /**
     * Create a recipe
     *
     * @param Recipe data
     *
     * @return Recipe
     */
    public static Recipe create(Recipe data) {
        return recipeDAO.create(data);
    }

    /**
     * Update a recipe
     *
     * @param Recipe data
     *
     * @return Recipe
     */
    public static Recipe update(Recipe data) {
        return recipeDAO.update(data);
    }

    /**
     * Find a recipe by id
     *
     * @param Integer id
     *
     * @return Recipe
     */
    public static Recipe find(Integer id) {
        return recipeDAO.find(id);
    }
    
    /**
     * Find a recipe by slug
     *
     * @param String slug
     *
     * @return Recipe
     */
    public static Recipe findBySlug(String slug) {
        return recipeDAO.findBySlug(slug);
    }

    /**
     * Delete a recipe by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Recipe recipe = recipeDAO.find(id);
        if (recipe != null) {
            recipeDAO.delete(recipe);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all recipes
     *
     * @return List<Recipe>
     */
    public static List<Recipe> all() {
        return recipeDAO.all();
    }

    /**
     * Get the page of recipes
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Recipe>
     */
    public static List<Recipe> paginate(Integer page, Integer size) {
        return recipeDAO.paginate(page, size);
    }

    /**
     * Get the number of total of recipes
     *
     * @return Long
     */
    public static Long count() {
        return recipeDAO.count();
    }

    /**
     * Add a tag to a recipe
     *
     * @param tag
     * @param recipe
     *
     * @return boolean
     */
    public static boolean addTag(Integer tagId, Integer recipeId) {
        Tag tag = TagService.find(tagId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (tag != null && recipe != null) {
            RecipeTags tagged = new RecipeTags(tag, recipe);
            if (!recipe.tags.contains(tagged)) {
                RecipeDAO.addTag(tag, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a tag of a recipe
     *
     * @param tag
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteTag(Integer tagId, Integer recipeId) {
        Tag tag = TagService.find(tagId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (tag != null && recipe != null) {
            RecipeTags tagged = new RecipeTags(tag, recipe);
            if (recipe.tags.contains(tagged)) {
                RecipeDAO.deleteTag(tag, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Add a recipe as a favorite
     *
     * @param user
     * @param recipe
     *
     * @return boolean
     */
    public static boolean addFavorite(Integer userId, Integer recipeId) {
        User user = UserService.find(userId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (user != null && recipe != null) {
            Favorite fav = new Favorite(user, recipe);
            if (!user.recipesFavorites.contains(fav)) {
                RecipeDAO.addFavorite(user, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a recipe favorite
     *
     * @param user
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteFavorite(Integer userId, Integer recipeId) {
        User user = UserService.find(userId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (user != null && recipe != null) {
            Favorite fav = new Favorite(user, recipe);
            if (user.recipesFavorites.contains(fav)) {
                RecipeDAO.deleteFavorite(user, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Add a rating of a recipe
     *
     * @param user
     * @param recipe
     * @param value
     *
     * @return boolean
     */
    public static boolean addRating(Integer userId, Integer recipeId, double value) {
        User user = UserService.find(userId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (user != null && recipe != null && value >= 0.0 && value <= 5.0) {
            Rating fav = new Rating(user, recipe);
            if (!user.ratings.contains(fav)) {
                RecipeDAO.addRating(user, recipe, value);
                return true;
            }
        }
        return false;
    }

    /**
     * Update a rating of a recipe
     *
     * @param user
     * @param recipe
     * @param value
     *
     * @return boolean
     */
    public static boolean updateRating(Integer userId, Integer recipeId, double value) {
        User user = UserService.find(userId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (user != null && recipe != null && value >= 0.0 && value <= 5.0) {
            Rating fav = new Rating(user, recipe);
            if (user.ratings.contains(fav)) {
                RecipeDAO.updateRating(user, recipe, value);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a rating of a recipe
     *
     * @param user
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteRating(Integer userId, Integer recipeId) {
        User user = UserService.find(userId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (user != null && recipe != null) {
            Rating fav = new Rating(user, recipe);
            if (user.ratings.contains(fav)) {
                RecipeDAO.deleteRating(user, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Add a section to a recipe
     *
     * @param category
     * @param recipe
     *
     * @return boolean
     */
    public static boolean addCategory(Integer categoryId, Integer recipeId) {
        Category category = CategoryService.find(categoryId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (category != null && recipe != null) {
            if (!category.recipes.contains(recipe)) {
                RecipeDAO.addOrUpdateCategory(category, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Update a section to a recipe
     *
     * @param category
     * @param recipe
     *
     * @return boolean
     */
    public static boolean updateCategory(Integer categoryId, Integer recipeId) {
        Category category = CategoryService.find(categoryId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (category != null && recipe != null) {
            RecipeDAO.addOrUpdateCategory(category, recipe);
            return true;
        }
        return false;
    }

    /**
     * Delete a section of a recipe
     *
     * @param category
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteCategory(Integer categoryId, Integer recipeId) {
        Category category = CategoryService.find(categoryId);
        Recipe recipe = recipeDAO.find(recipeId);
        if (category != null && recipe != null) {
            if (category.recipes.contains(recipe)) {
                RecipeDAO.deleteCategory(recipe);
                return true;
            }
        }
        return false;
    }
}
