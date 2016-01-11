package models.base;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.persistence.NoResultException;

import play.db.jpa.JPA;

public class CrudDAO<T extends Model> {
    private Class<? extends Model> typeOfModel;
    protected String               TABLE;

    public CrudDAO(Class<? extends Model> model) {
        typeOfModel = model;
        TABLE = typeOfModel.getName();
    }

    /**
     * Create a model
     *
     * @param Model model
     * @return Model
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     */
    @SuppressWarnings("unchecked")
    public T create(Model model) {
        model.prePersistData();
        JPA.em().persist(model);
        model.postPersistData(true);
        // Flush and refresh for check
        JPA.em().flush();
        JPA.em().refresh(model);
        return (T) model;
    }

    /**
     * Find a model by id
     *
     * @param Integer id
     * @return Model
     */
    @SuppressWarnings("unchecked")
    public T find(Integer id) {
        return (T) JPA.em().find(typeOfModel, id);
    }


    
    /**
     * Find by field
     *
     * @param field
     * @param value
     *
     * @return Tag
     */
    @SuppressWarnings("unchecked")
    public T findBy(String field, String value) {
        try {
            return (T) JPA.em().createQuery("SELECT m FROM " + TABLE + " m WHERE " + field + " = '" + value + "'").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        
    }
    
    /**
     * Update a model
     *
     * @param Model model
     * @return Model
     */
    @SuppressWarnings("unchecked")
    public T update(Model model) {
        Model aux = find(model.id);
        model.handleRelations(aux);
        model.prePersistData();
        JPA.em().merge(model);
        model.postPersistData(false);
        return (T) model;
    }

    /**
     * Delete a model by id
     *
     * @param Model model
     */
    public void delete(Model model) {
        JPA.em().remove(model);
    }

    /**
     * Get all models
     *
     * @return List<Model>
     */
    @SuppressWarnings("unchecked")
    public List<T> all() {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").getResultList();
    }

    /**
     * Get the page of models
     *
     * @param Integer page
     * @param Integer size
     * @return List<Model>
     */
    @SuppressWarnings("unchecked")
    public List<T> paginate(Integer page, Integer size) {
        return JPA.em().createQuery("SELECT m FROM " + TABLE + " m ORDER BY id").setFirstResult(page * size)
                .setMaxResults(size).getResultList();
    }

    /**
     * Get the number of total row
     *
     * @return Long
     */
    public Long count() {
        return (Long) JPA.em().createQuery("SELECT count(m) FROM " + TABLE + " m").getSingleResult();
    }
}
