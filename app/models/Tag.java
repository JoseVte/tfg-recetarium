package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.dao.TagDAO;
import models.manytomany.RecipeTags;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Timestamp;
import util.TimestampListener;

@Entity
@EntityListeners({ TimestampListener.class })
@Table(name = "tags")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Tag extends Timestamp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @Constraints.Required
    @Column(unique = true, nullable = false)
    public String             text;

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<RecipeTags>   recipes          = new ArrayList<RecipeTags>();

    public Tag() {
    }

    public Tag(String text) {
        this.text = text;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!TagDAO.check("text", text, id).isEmpty()) {
            errors.add(new ValidationError("text", "This tag is already created."));
        }
        return errors.isEmpty() ? null : errors;
    }

    public void prePersistData() {
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
