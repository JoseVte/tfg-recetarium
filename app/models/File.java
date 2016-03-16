package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import models.base.Model;
import models.manytomany.RecipeFiles;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import util.serializer.CountRecipeFilesSerializer;
import util.serializer.CountRecipesSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@JsonPropertyOrder({"id", "url", "type", "title", "new_title", "user", "recipesImageMain", "recipes", "created_at", "updated_at"})
@Table(name = "files")
public class File extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    public String url;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String title;

    @Column(name = "new_title", nullable = false)
    @JsonProperty(value = "new_title")
    public String newTitle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = CountRecipesSerializer.class)
    @OneToMany(mappedBy = "imageMain", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Recipe> recipesImageMain = new ArrayList<Recipe>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = CountRecipeFilesSerializer.class)
    @OneToMany(mappedBy = "file", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<RecipeFiles> recipes = new ArrayList<RecipeFiles>();

    public File() {}

    public File(String url, String type, String title, String newTitle, User user) {
        this.url = url;
        this.type = type;
        this.title = title;
        this.newTitle = newTitle;
        this.user = user;
    }

    @Override
    public void handleRelations(Model old) {
        File model = (File) old;
        this.setCreatedAt(model.getCreatedAt());
        this.recipes = model.recipes;
        this.recipesImageMain = model.recipesImageMain;
    }
}
