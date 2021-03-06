package models.service;

import models.Recipe;
import models.Tag;
import models.dao.TagDAO;
import models.manytomany.RecipeTags;

import java.util.ArrayList;
import java.util.List;

public class TagService {
    private static TagDAO dao;

    static {
        dao = new TagDAO();
    }

    /**
     * Create a tag
     *
     * @param data Tag
     *
     * @return Tag
     */
    public static Tag create(Tag data) {
        return dao.create(data);
    }

    /**
     * Create all tags from names
     *
     * @param names List<String>
     *
     * @return List<Integer>
     */
    public static List<Integer> create(List<String> names) {
        List<Tag> tags = new ArrayList<Tag>();
        for (String name : names) {
            tags.add(new Tag(name));
        }
        return dao.create(tags);
    }

    /**
     * Update a tag
     *
     * @param data Tag
     *
     * @return Tag
     */
    public static Tag update(Tag data) {
        return dao.update(data);
    }

    /**
     * Find a tag by id
     *
     * @param id Integer
     *
     * @return Tag
     */
    public static Tag find(Integer id) {
        return dao.find(id);
    }

    /**
     * Delete a tag by id
     *
     * @param id Integer
     */
    public static Boolean delete(Integer id) {
        Tag tag = dao.find(id);
        if (tag != null) {
            dao.delete(tag);
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
        return dao.all();
    }

    /**
     * Get the page of tags
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<Tag>
     */
    public static List<Tag> paginate(Integer page, Integer size) {
        return dao.paginate(page, size);
    }

    /**
     * Search all tags
     *
     * @param search String
     *
     * @return List<Tag>
     */
    public static List<Tag> search(String search) {
        return dao.search(search);
    }

    /**
     * Get the number of total of tags
     *
     * @return Long
     */
    public static Long count() {
        return dao.count();
    }

    public static List<Integer> containAll(List<Integer> tagIds) {
        return dao.containAll(tagIds);
    }

    /**
     * Add a recipe to a tag
     *
     * @param tagId    Integer
     * @param recipeId Integer
     *
     * @return boolean
     */
    public static boolean addRecipe(Integer tagId, Integer recipeId) {
        Tag tag = dao.find(tagId);
        Recipe recipe = RecipeService.find(recipeId);
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
     * @param tagId    Integer
     * @param recipeId Integer
     *
     * @return boolean
     */
    public static boolean deleteRecipe(Integer tagId, Integer recipeId) {
        Tag tag = dao.find(tagId);
        Recipe recipe = RecipeService.find(recipeId);
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
