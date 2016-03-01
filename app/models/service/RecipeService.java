package models.service;

import controllers.RecipeController.IngredientRequest;
import controllers.RecipeController.RecipeRequest;
import models.*;
import models.dao.RecipeDAO;
import models.enums.RecipeDifficulty;
import models.enums.RecipeVisibility;
import models.manytomany.Favorite;
import models.manytomany.Rating;
import models.manytomany.RecipeFiles;
import models.manytomany.RecipeTags;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecipeService {
    private static RecipeDAO recipeDAO;

    static {
        recipeDAO = new RecipeDAO();
    }

    /**
     * Create a recipe
     *
     * @param data Recipe
     *
     * @return Recipe
     */
    public static Recipe create(Recipe data) {
        return recipeDAO.create(data);
    }

    /**
     * Create a recipe
     *
     * @param data RecipeRequest
     *
     * @return Recipe
     */
    public static Recipe create(RecipeRequest data) {
        return recipeDAO.create(new Recipe(data));
    }

    public static Recipe createDraft(User user) {
        Long lastId = recipeDAO.countNumberByUser(user) + 1;
        Recipe recipe = new Recipe("recipe-" + user.username + "-" + lastId, "Recipe " + user.username + " " + lastId, null, new Date(0), RecipeDifficulty.EASY, 0, user, null, RecipeVisibility.PUBLIC);
        recipe.isDraft = true;
        return recipeDAO.create(recipe);
    }


    public static Recipe getDraft(User user) {
        return recipeDAO.getDraft(user);
    }

    /**
     * Update a recipe
     *
     * @param data Recipe
     *
     * @return Recipe
     */
    public static Recipe update(Recipe data) {
        return recipeDAO.update(data);
    }

    /**
     * Update a recipe
     *
     * @param data RecipeRequest
     *
     * @return Recipe
     */
    public static Recipe update(RecipeRequest data) {
        return recipeDAO.update(new Recipe(data));
    }

    /**
     * Find a recipe by id
     *
     * @param id Integer
     *
     * @return Recipe
     */
    public static Recipe find(Integer id) {
        return recipeDAO.find(id);
    }

    /**
     * Find a recipe by slug
     *
     * @param slug String
     *
     * @return Recipe
     */
    public static Recipe findBySlug(String slug) {
        return recipeDAO.findBySlug(slug);
    }

    /**
     * Find a recipe by slug
     *
     * @param slug String
     * @param user String
     *
     * @return Recipe
     */
    public static Recipe findBySlug(String slug, String user) {
        return recipeDAO.findBySlug(slug, user);
    }

    /**
     * Find a recipe by slug and id
     *
     * @param slug String
     *
     * @return Recipe
     */
    public static Recipe findBySlugAndId(String slug, Integer id) {
        return recipeDAO.findBySlugAndId(slug, id);
    }

    /**
     * Find a recipe by slug
     *
     * @param email    String
     * @param idRecipe IntegerRecipe
     *
     * @return Recipe
     */
    public static Recipe findByOwner(String email, Integer idRecipe) {
        return recipeDAO.findByOwner(email, idRecipe);
    }

    /**
     * Delete a recipe by id
     *
     * @param email String
     * @param id    Integer
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
     * @param search String
     * @param page   Integer
     * @param size   Integer
     * @param user   String
     *
     * @return List<Recipe>
     */
    public static List<Recipe> paginate(Integer page, Integer size, String search, String user) {
        return recipeDAO.paginate(page, size, search, user);
    }

    /**
     * Get the number of total of searched recipes
     *
     * @param search String
     * @param user   String
     *
     * @return Long
     */
    public static Long count(String search, String user) {
        return recipeDAO.count(search, user);
    }

    /**
     * Get a recipe if this is the owner
     *
     * @param email    String
     * @param idRecipe IntegerRecipe
     *
     * @return boolean
     */
    public static boolean checkOwner(String email, Integer idRecipe) {
        return null != recipeDAO.findByOwner(email, idRecipe);
    }

    /**
     * Add a tag to a recipe
     *
     * @param tagId    Integer
     * @param recipeId Integer
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
     * Add all tags into a recipe
     *
     * @param tagIds   List<Integer>
     * @param recipeId Integer
     */
    public static void addTags(List<Integer> tagIds, Integer recipeId) {
        Recipe recipe = recipeDAO.find(recipeId);
        if (recipe != null) {
            List<Tag> tags = new ArrayList<Tag>();
            for (Integer tagId : tagIds) {
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
     * @param tagId    Integer
     * @param recipeId Integer
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
     * Delete all tags of a recipe
     *
     * @param recipeId Integer
     *
     * @return boolean
     */
    public static boolean deleteTags(Integer recipeId) {
        Recipe recipe = recipeDAO.find(recipeId);
        if (recipe != null) {
            RecipeDAO.deleteTags(recipe);
            return true;
        }
        return false;
    }

    /**
     * Sync the files into a recipe
     *
     * @param filesId List<Integer>
     * @param recipeId Integer
     */
    public static void syncFiles(List<Integer> filesId, Integer recipeId) {
        Recipe recipe = recipeDAO.find(recipeId);
        syncFiles(filesId, recipe);
    }

    /**
     * Sync the files into a recipe
     *
     * @param filesId List<Integer>
     * @param recipe Recipe
     */
    public static void syncFiles(List<Integer> filesId, Recipe recipe) {
        if (!filesId.isEmpty() && recipe != null) {
            RecipeDAO.deleteFiles(recipe);
            recipe.files.clear();
            for(Integer fileId : filesId) {
                recipe.files.add(new RecipeFiles(recipe, FileService.find(fileId)));
            }
            RecipeDAO.syncFiles(recipe);
        }
    }

    /**
     * Add a recipe as a favorite
     *
     * @param userId   Integer
     * @param recipeId Integer
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
     * @param userId   Integer
     * @param recipeId Integer
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
     * @param userId   Integer
     * @param recipeId Integer
     * @param value    double
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
     * @param userId   Integer
     * @param recipeId Integer
     * @param value    double
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
     * @param userId   Integer
     * @param recipeId Integer
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
     * @param categoryId Category
     * @param recipeId   Integer
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
     * @param categoryId Category
     * @param recipeId   Integer
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
     * @param categoryId Category
     * @param recipeId   Integer
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

    /**
     * Add an ingredient to a recipe
     *
     * @param recipeId          Integer
     * @param ingredientRequest IngredientRequest
     *
     * @return Ingredient
     */
    public static Ingredient addIngredient(Integer recipeId, IngredientRequest ingredientRequest) {
        Recipe recipe = recipeDAO.find(recipeId);
        Ingredient ingredient = new Ingredient(ingredientRequest);
        if (recipe != null) {
            ingredient.recipe = recipe;
            IngredientService.create(ingredient);
            return ingredient;
        }
        return null;
    }

    /**
     * Update the ingredients for a recipe
     *
     * @param recipe Recipe
     */
    public static void updateIngredients(Recipe recipe) {
        recipeDAO.deleteIngredients(recipe);
        recipeDAO.addIngredients(recipe);
    }

    /**
     * Add an ingredient to a recipe
     *
     * @param recipeId     Integer
     * @param ingredientId Integer
     *
     * @return boolean
     */
    public static boolean deleteIngredient(Integer recipeId, Integer ingredientId) {
        Recipe recipe = recipeDAO.find(recipeId);
        Ingredient ingredient = IngredientService.find(ingredientId);
        if (recipe != null && ingredient != null) {
            if (recipe.ingredients.contains(ingredient)) {
                IngredientService.delete(ingredientId);
                return true;
            }
        }
        return false;
    }
}
