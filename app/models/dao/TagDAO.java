package models.dao;

import java.util.List;

import models.Recipe;
import models.Tag;
import models.base.CrudDAO;
import models.manytomany.RecipeTags;
import play.db.jpa.JPA;

public class TagDAO extends CrudDAO<Tag> {
    public TagDAO() {
        super(Tag.class);
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     * @param String comparison
     *
     * @return List<Tag>
     */
    public List<Tag> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " "
                + comparison + " '" + value + "' ORDER BY id", Tag.class).getResultList();
    }

    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     * @param Integer id
     *
     * @return List<Tag>
     */
    public List<Tag> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }

    /**
     * Add a tag to a recipe
     *
     * @param tag
     * @param recipe
     */
    public static void addRecipe(Tag tag, Recipe recipe) {
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
    public static void deleteRecipe(Tag tag, Recipe recipe) {
        RecipeTags tagged = JPA.em().createQuery("SELECT m FROM " + RecipeTags.class.getName() + " m WHERE tag_id = "
                + tag.id + " AND recipe_id = " + recipe.id, RecipeTags.class).getSingleResult();
        JPA.em().remove(tagged);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(tag);
        JPA.em().refresh(recipe);
    }
}
