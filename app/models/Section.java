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

import models.dao.SectionDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Timestamp;
import util.TimestampListener;

@Entity
@EntityListeners({ TimestampListener.class })
@Table(name = "sections")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Section extends Timestamp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @Constraints.Required
    @Column(unique = true, nullable = false)
    public String             text;

    @OneToMany(mappedBy = "section", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Recipe>       recipes          = new ArrayList<Recipe>();

    public Section() {
    }

    public Section(String text) {
        this.text = text;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!SectionDAO.check("text", text, id).isEmpty()) {
            errors.add(new ValidationError("text", "This section is already created."));
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
        return "Section [id=" + id + ", text=" + text + ", recipes=" + recipes.size() + "]";
    }

}
