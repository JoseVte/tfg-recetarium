package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import models.base.Model;
import models.dao.MediaDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

@Entity
@JsonPropertyOrder({ "id", "filename", "recipe", "created_at", "updated_at" })
@Table(name = "media", uniqueConstraints = { @UniqueConstraint(columnNames = { "filename", "recipe_id" }) })
public class Media extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    public String             filename;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    public Recipe             recipe;

    public Media() {
        dao = new MediaDAO();
    }

    public Media(String filename, Recipe recipe) {
        dao = new MediaDAO();
        this.filename = filename;
        this.recipe = recipe;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!((MediaDAO) dao).check(recipe.id, filename, id).isEmpty()) {
            errors.add(new ValidationError("filename", "This filename is already used for this recipe."));
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
        Media media = (Media) old;
        this.setCreatedAt(media.getCreatedAt());
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Media [id=" + id + ", filename=" + filename + ", recipe=" + recipe.id + "]";
    }
}
