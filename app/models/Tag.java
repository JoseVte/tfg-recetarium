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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import models.base.Model;
import models.dao.TagDAO;
import models.manytomany.RecipeTags;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

@Entity
@Table(name = "tags")
@JsonPropertyOrder({ "id", "text", "created_at", "updated_at" })
public class Tag extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    @Column(unique = true, nullable = false)
    public String             text;

    @JsonIgnore
    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<RecipeTags>   recipes          = new ArrayList<RecipeTags>();

    public Tag() {
        dao = new TagDAO();
    }

    public Tag(String text) {
        dao = new TagDAO();
        this.text = text;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!((TagDAO) dao).check("text", text, id).isEmpty()) {
            errors.add(new ValidationError("text", "This tag is already created."));
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
        Tag tag = (Tag) old;
        this.setCreatedAt(tag.getCreatedAt());
        this.recipes = tag.recipes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Tag [id=" + id + ", text=" + text + ", recipes=" + recipes.size() + "]";
    }
}
