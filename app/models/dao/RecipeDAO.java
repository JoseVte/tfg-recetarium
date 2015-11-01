package models.dao;

import java.util.List;

import models.Recipe;
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
    public static Recipe create (Recipe model) {
        model.emptyToNull();
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
     * @param Integer id
     */
    public static void delete(Integer id) {
        Recipe model = JPA.em().getReference(Recipe.class, id);
        JPA.em().remove(model);
    }

    /**
     * Get all recipes
     *
     * @return List<Recipe>
     */
    @SuppressWarnings("unchecked")
	public static List<Recipe> all() {
        return (List<Recipe>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
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
        return (List<Recipe>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page*size).setMaxResults(size).getResultList();
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
     * @param String comparison
     *
     * @return List<Recipe>
     */
    @SuppressWarnings("unchecked")
	public static List<Recipe> check(String field, Object value, Integer id, String comparison) {
    	return (List<Recipe>) JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE id != " +id + " AND " + field + " " + comparison + " '" + value + "' ORDER BY id").getResultList();
    }
    
    /**
     * Where clause
     *
     * @param String field
     * @param Object value
     *
     * @return List<Recipe>
     */
	public static List<Recipe> check(String field, Object value, Integer id) {
    	return check(field, value, id, "=");
    }
}