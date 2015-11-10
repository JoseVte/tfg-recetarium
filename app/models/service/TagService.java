package models.service;

import java.util.List;

import models.Recipe;
import models.Tag;
import models.dao.RecipeDAO;
import models.dao.TagDAO;
import models.manytomany.RecipeTags;

public class TagService {
    /**
     * Create a tag
     *
     * @param Tag data
     *
     * @return Tag
     */
    public static Tag create(Tag data) {
        return TagDAO.create(data);
    }

    /**
     * Update a tag
     *
     * @param Tag data
     *
     * @return Tag
     */
    public static Tag update(Tag data) {
        return TagDAO.update(data);
    }

    /**
     * Find a tag by id
     *
     * @param Integer id
     *
     * @return Tag
     */
    public static Tag find(Integer id) {
        return TagDAO.find(id);
    }

    /**
     * Delete a tag by id
     *
     * @param Integer id
     */
    public static Boolean delete(Integer id) {
        Tag tag = TagDAO.find(id);
        if (tag != null) {
            TagDAO.delete(tag);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get all tags
     *
     * @return List<Tag>
     */
    public static List<Tag> all() {
        return TagDAO.all();
    }

    /**
     * Get the page of tags
     *
     * @param Integer page
     * @param Integer size
     *
     * @return List<Tag>
     */
    public static List<Tag> paginate(Integer page, Integer size) {
        return TagDAO.paginate(page, size);
    }

    /**
     * Get the number of total of tags
     *
     * @return Long
     */
    public static Long count() {
        return TagDAO.count();
    }

    /**
     * Add a recipe to a tag
     *
     * @param tag
     * @param recipe
     *
     * @return boolean
     */
    public static boolean addRecipe(Integer tagId, Integer recipeId) {
        Tag tag = TagDAO.find(tagId);
        Recipe recipe = RecipeDAO.find(recipeId);
        if (tag != null && recipe != null) {
            RecipeTags tagged = new RecipeTags(tag, recipe);
            if (!tag.recipes.contains(tagged)) {
                TagDAO.addRecipe(tag, recipe);
                return true;
            }
        }
        return false;
    }

    /**
     * Delete a recipe of a tag
     *
     * @param tag
     * @param recipe
     *
     * @return boolean
     */
    public static boolean deleteRecipe(Integer tagId, Integer recipeId) {
        Tag tag = TagDAO.find(tagId);
        Recipe recipe = RecipeDAO.find(recipeId);
        if (tag != null && recipe != null) {
            RecipeTags tagged = new RecipeTags(tag, recipe);
            if (tag.recipes.contains(tagged)) {
                TagDAO.deleteRecipe(tag, recipe);
                return true;
            }
        }
        return false;
    }
}
