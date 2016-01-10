package models.service;

import java.util.ArrayList;
import java.util.List;

import controllers.RecipeController.RecipeRequest;
import models.Category;
import models.Recipe;
import models.Tag;
import models.User;
import models.dao.RecipeDAO;
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
     * @return Recipe
     */
    public static Recipe create(Recipe data) {
        return recipeDAO.create(data);
    }

    /**
     * Create a recipe
     *
     * @param RecipeRequest data
     * @return Recipe
     */
    public static Recipe create(RecipeRequest data) {
        return recipeDAO.create(new Recipe(data));
    }

    /**
     * Update a recipe
     *
     * @param Recipe data
     * @return Recipe
     */
    public static Recipe update(Recipe data) {
        return recipeDAO.update(data);
    }

    /**
     * Update a recipe
     *
     * @param RecipeRequest data
     * @return Recipe
     */
    public static Recipe update(RecipeRequest data) {
        return recipeDAO.update(new Recipe(data));
    }

    /**
     * Find a recipe by id
     *
     * @param Integer id
     * @return Recipe
     */
    public static Recipe find(Integer id) {
        return recipeDAO.find(id);
    }

    /**
     * Find a recipe by slug
     *
     * @param String slug
     * @return Recipe
     */
    public static Recipe findBySlug(String slug) {
        return recipeDAO.findBySlug(slug);
    }
    
    /**
     * Find a recipe by slug and id
     *
     * @param String slug
     * @return Recipe
     */
    public static Recipe findBySlugAndId(String slug, Integer id) {
        return recipeDAO.findBySlugAndId(slug, id);
    }

    /**
     * Find a recipe by slug
     *
     * @param String email
     * @param Integer idRecipe
     * @return Recipe
     */
    public static Recipe findByOwner(String email, Integer idRecipe) {
        return recipeDAO.findByOwner(email, idRecipe);
    }

    /**
     * Delete a recipe by id
     *
     * @param String email
     * @param Integer id
     */
    public static Boolean delete(Integer id, String email) {
        Recipe recipe = recipeDAO.findByOwner(email, id);
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
     * Get a recipe if this is the owner
     *
     * @param String email
     * @param Integer idRecipe
     * @return boolean
     */
    public static boolean checkOwner(String email, Integer idRecipe) {
        return null != recipeDAO.findByOwner(email, idRecipe);
    }

    /**
     * Add a tag to a recipe
     *
     * @param tag
     * @param recipe
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
     * Add all tags into a recipe
     *
     * @param tagIds
     * @param recipeId
     */
    public static void addTags(List<Integer> tagIds, Integer recipeId) {
        Recipe recipe = recipeDAO.find(recipeId);
        if (recipe != null) {
            List<Tag> tags = new ArrayList<Tag>();
            for(Integer tagId : tagIds) {
                Tag tag = new Tag();
                tag.id = tagId;
                RecipeTags tagged = new RecipeTags(tag, recipe);
                if (!recipe.tags.contains(tagged)) {
                    tags.add(tag);
                }
            }
            RecipeDAO.addTags(tags, recipe);
        }
    }

    /**
     * Delete a tag of a recipe
     *
     * @param tag
     * @param recipe
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

    /**
     * Update the ingredients for a recipe
     *
     * @param recipe
     */
    public static void updateIngredients(Recipe recipe) {
        recipeDAO.deleteIngredients(recipe);
        recipeDAO.addIngredients(recipe);
    }
}
