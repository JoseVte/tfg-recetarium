package models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import models.base.Model;

@Entity
@Table(name = "ingredients")
@JsonPropertyOrder({ "id", "name", "count", "recipe", "created_at", "updated_at" })
public class Ingredient extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    public String             name;
    public String             count;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    public Recipe             recipe;

    public Ingredient() {
    }

    public Ingredient(String name, String count) {
        super();
        this.name = name;
        this.count = count;
    }

    @Override
    public void handleRelations(Model old) {
        Media media = (Media) old;
        this.setCreatedAt(media.getCreatedAt());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((count == null) ? 0 : count.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (getClass() != obj.getClass()) return false;
        Ingredient other = (Ingredient) obj;
        if (count == null) {
            if (other.count != null) return false;
        } else if (!count.equals(other.count)) return false;
        if (name == null) {
            if (other.name != null) return false;
        } else if (!name.equals(other.name)) return false;
        return true;
    }
}
