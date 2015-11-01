package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.dao.MediaDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.*;

@Entity
@EntityListeners({
        CreatedAtListener.class,
        UpdatedAtListener.class
})
@Table(name = "media", uniqueConstraints={@UniqueConstraint(columnNames={"filename","recipe_id"})})
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Media implements Creatable, Updatable {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

	@Constraints.Required
	@Column(nullable = false)
    public String filename;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id")
    public Recipe recipe;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Override
    public void setCreatedAt(Date date) {
        this.createdAt = date;
    }

    @Override
    public void setUpdatedAt(Date date) {
        this.updatedAt = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    public Media () {}
    
    public Media(String filename) {
    	this.filename = filename;
    }
    
    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!MediaDAO.check(recipe.id, filename, id).isEmpty()) {
            errors.add(new ValidationError("filename", "This filename is already used for this recipe."));
        }
        return errors.isEmpty() ? null : errors;
    }
}
