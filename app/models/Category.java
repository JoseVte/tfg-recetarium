package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.dao.CategoryDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Model;

@Entity
@Table(name = "categories")
public class Category extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    @Column(unique = true, nullable = false)
    public String             text;

    @JsonIgnore
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Recipe>       recipes          = new ArrayList<Recipe>();

    public Category() {
    }

    public Category(String text) {
        this.text = text;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!CategoryDAO.check("text", text, id).isEmpty()) {
            errors.add(new ValidationError("text", "This section is already created."));
        }
        return errors.isEmpty() ? null : errors;
    }

    /* (non-Javadoc)
     * @see util.Model#prePersistData()
     */
    public void prePersistData() {
    }
    
    /* (non-Javadoc)
     * @see util.Model#handleRelations(util.Model old)
     */
    public void handleRelations(Model old) {
        Category category = ((Category)old);
        this.setCreatedAt(category.getCreatedAt());
        this.recipes = category.recipes;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Category)) return false;
        Category other = (Category) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (text == null) {
            if (other.text != null) return false;
        } else if (!text.equals(other.text)) return false;
        return true;
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

}
