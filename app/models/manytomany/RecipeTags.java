package models.manytomany;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.Recipe;
import models.Tag;

@Entity
@Table(name = "recipe_tags", uniqueConstraints = { @UniqueConstraint(columnNames = { "tag_id", "recipe_id" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class RecipeTags implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @ManyToOne
    @JoinColumn(name = "tag_id")
    public Tag                tag;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    public Recipe             recipe;

    public RecipeTags() {
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id);
        result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RecipeTags other = (RecipeTags) obj;
        if (recipe == null) {
            if (other.recipe != null) return false;
        } else if (!recipe.equals(other.recipe)) return false;
        if (tag == null) {
            if (other.tag != null) return false;
        } else if (!tag.equals(other.tag)) return false;
        return true;
    }

    public void prePersistData() {
    }
}
