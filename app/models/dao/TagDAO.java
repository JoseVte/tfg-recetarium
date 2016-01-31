package models.dao;

import models.Recipe;
import models.Tag;
import models.base.CrudDAO;
import models.manytomany.RecipeTags;
import play.db.jpa.JPA;

import java.util.ArrayList;
import java.util.List;

public class TagDAO extends CrudDAO<Tag> {
    public TagDAO() {
        super(Tag.class);
    }

    /**
     * Add a tag to a recipe
     *
     * @param tag    Tag
     * @param recipe Recipe
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
     * @param tag    Tag
     * @param recipe Recipe
     */
    public static void deleteRecipe(Tag tag, Recipe recipe) {
        RecipeTags tagged = JPA.em().createQuery("SELECT m FROM " + RecipeTags.class.getName() + " m WHERE tag_id = " + tag.id + " AND recipe_id = " + recipe.id, RecipeTags.class).getSingleResult();
        JPA.em().remove(tagged);
        // Reload entities
        JPA.em().flush();
        JPA.em().refresh(tag);
        JPA.em().refresh(recipe);
    }

    /**
     * Create all tags
     *
     * @param tags List<Tag>
     *
     * @return List<Integer>
     */
    public List<Integer> create(List<Tag> tags) {
        List<Integer> ids = new ArrayList<Integer>();
        Tag aux;
        for (Tag tag : tags) {
            aux = findBy("text", tag.text);
            if (aux == null) {
                create(tag);
            } else {
                tag = aux;
            }

            ids.add(tag.id);
        }
        return ids;
    }

    /**
     * Where clause
     *
     * @param field      String
     * @param value      Object
     * @param id         Integer
     * @param comparison String
     *
     * @return List<Tag>
     */
    public List<Tag> check(String field, Object value, Integer id, String comparison) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " + id + " AND " + field + " " + comparison + " '" + value + "' ORDER BY id", Tag.class).getResultList();
    }

    /**
     * Where clause
     *
     * @param field String
     * @param value Object
     * @param id    Integer
     *
     * @return List<Tag>
     */
    public List<Tag> check(String field, Object value, Integer id) {
        return check(field, value, id, "=");
    }

    /**
     * Search all tags
     *
     * @param search String
     *
     * @return List<Tag>
     */
    public List<Tag> search(String search) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE text like '%" + search + "%'", Tag.class).getResultList();
    }

    public List<Integer> containAll(List<Integer> tagIds) {
        List<Integer> aux = JPA.em().createQuery("SELECT m.id FROM " + TABLE + " m WHERE m.id IN :list", Integer.class).setParameter("list", tagIds).getResultList();
        tagIds.removeAll(aux);
        return tagIds;
    }
}
