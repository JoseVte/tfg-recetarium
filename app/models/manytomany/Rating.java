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
import models.User;

@Entity
@Table(name = "ratings", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "recipe_id" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Rating implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User               user;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    public Recipe             recipe;

    @Column(precision = 10, scale = 2)
    public Double             rating           = 0.0;

    public Rating() {
    }

    public Rating(User user, Recipe recipe) {
        this.user = user;
        this.recipe = recipe;
    }

    public Rating(User user, Recipe recipe, Double rating) {
        this.user = user;
        this.recipe = recipe;
        this.rating = rating;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id);
        result = prime * result + ((rating == null) ? 0 : rating.hashCode());
        result = prime * result + ((recipe == null) ? 0 : recipe.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Rating other = (Rating) obj;
        if (recipe == null) {
            if (other.recipe != null) return false;
        } else if (!recipe.equals(other.recipe)) return false;
        if (user == null) {
            if (other.user != null) return false;
        } else if (!user.equals(other.user)) return false;
        return true;
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
        return "Rating [id=" + id + ", user=" + user.id + ", recipe=" + recipe.id + ", rating=" + rating + "]";
    }

}
