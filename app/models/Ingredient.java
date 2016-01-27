package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import controllers.RecipeController.IngredientRequest;
import models.base.Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "ingredients")
@JsonPropertyOrder({"id", "name", "count", "recipe", "created_at", "updated_at"})
public class Ingredient extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    public String name;
    public String count;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    public Recipe recipe;

    public Ingredient() {
    }

    public Ingredient(String name, String count) {
        super();
        this.name = name;
        this.count = count;
    }

    public Ingredient(IngredientRequest ingredientRequest) {
        super();
        this.id = ingredientRequest.id;
        this.name = ingredientRequest.name;
        this.count = ingredientRequest.count;
    }

    @Override
    public void handleRelations(Model old) {
        Ingredient ingredient = (Ingredient) old;
        this.setCreatedAt(ingredient.getCreatedAt());
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
