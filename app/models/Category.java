package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.base.Model;
import models.dao.CategoryDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@JsonPropertyOrder({"id", "text", "created_at", "updated_at"})
public class Category extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    @Column(unique = true, nullable = false)
    public String text;

    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    public List<Recipe> recipes = new ArrayList<Recipe>();

    public Category() {
        dao = new CategoryDAO();
    }

    public Category(String text) {
        dao = new CategoryDAO();
        this.text = text;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!((CategoryDAO) dao).check("text", text, id).isEmpty()) {
            errors.add(new ValidationError("text", "This section is already created."));
        }
        return errors.isEmpty() ? null : errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#prePersistData()
     */
    @Override
    public void prePersistData() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        Category category = ((Category) old);
        this.setCreatedAt(category.getCreatedAt());
        this.recipes = category.recipes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Section [id=" + id + ", text=" + text + ", recipes=" + recipes.size() + "]";
    }

    @PreRemove
    private void preRemove() {
        for (Recipe recipe : recipes) {
            recipe.category = null;
        }
    }
}
