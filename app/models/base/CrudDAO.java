package models.base;

import play.db.jpa.JPA;

import javax.persistence.Column;
import javax.persistence.NoResultException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class CrudDAO<T extends Model> {
    protected String TABLE;
    private Class<? extends Model> typeOfModel;

    public CrudDAO(Class<? extends Model> model) {
        typeOfModel = model;
        TABLE = typeOfModel.getName();
    }

    /**
     * Get all columns availables for this model
     *
     * @return List<String>
     */
    public List<String> columns() {
        List<String> columns = new ArrayList<String>() {{
            add("id");
            add("createdAt");
            add("updatedAt");
        }};
        Field[] fields = typeOfModel.getFields();

        for (Field field: fields) {
            Annotation[] annotations = field.getDeclaredAnnotations();
            for(Annotation annotation : annotations){
                if(annotation instanceof Column) {
                    columns.add(field.getName());
                }
            }
        }

        return columns;
    }

    /**
     * Create a model
     *
     * @param model Model
     *
     * @return Model
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
     * @param id Integer
     *
     * @return Model
     */
    @SuppressWarnings("unchecked")
    public T find(Integer id) {
        return (T) JPA.em().find(typeOfModel, id);
    }


    /**
     * Find by field
     *
     * @param field String
     * @param value String
     *
     * @return Tag
     */
    @SuppressWarnings("unchecked")
    public T findBy(String field, String value) {
        try {
            return (T) JPA.em().createQuery("SELECT model FROM " + TABLE + " model WHERE " + field + " = '" + value + "'").getSingleResult();
        } catch (NoResultException e) {
            return null;
        }

    }

    /**
     * Update a model
     *
     * @param model Model
     *
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
     * @param model Model
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
        return JPA.em().createQuery("SELECT model FROM " + TABLE + " model ORDER BY model.id").getResultList();
    }

    /**
     * Get the page of models
     *
     * @param page Integer
     * @param size Integer
     *
     * @return List<Model>
     */
    @SuppressWarnings("unchecked")
    public List<T> paginate(Integer page, Integer size) {
        return JPA.em().createQuery("SELECT model FROM " + TABLE + " model ORDER BY model.id").setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    /**
     * Get the page of models order by
     *
     * @param page  Integer
     * @param size  Integer
     * @param order String
     *
     * @return List<Model>
     */
    @SuppressWarnings("unchecked")
    public List<T> paginate(Integer page, Integer size, String order) {
        return JPA.em().createQuery("SELECT model FROM " + TABLE + " model ORDER BY " + order).setFirstResult(page * size).setMaxResults(size).getResultList();
    }

    /**
     * Get the number of total row
     *
     * @return Long
     */
    public Long count() {
        return JPA.em().createQuery("SELECT count(model) FROM " + TABLE + " model", Long.class).getSingleResult();
    }
}
