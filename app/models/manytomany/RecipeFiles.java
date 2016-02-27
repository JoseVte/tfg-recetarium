package models.manytomany;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import models.File;
import models.Recipe;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "recipe_files", uniqueConstraints = {@UniqueConstraint(columnNames = {"recipe_id", "file_id"})})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class RecipeFiles implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    public Recipe recipe;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "file_id")
    public File file;

    public RecipeFiles() {}

    public RecipeFiles(Recipe recipe, File file) {
        this.recipe = recipe;
        this.file = file;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id);
        result = prime * result + ((file == null) ? 0 : file.hashCode());
        result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        RecipeFiles other = (RecipeFiles) obj;
        if (file == null) {
            if (other.file != null) return false;
        } else if (!file.equals(other.file)) return false;
        if (recipe == null) {
            if (other.recipe != null) return false;
        } else if (!recipe.equals(other.recipe)) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "RecipeFiles [id=" + id + ", recipe=" + recipe.id + ", file=" + file.id + "]";
    }
}
