package models;

import util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.dao.UserDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

@Entity
@EntityListeners({ TimestampListener.class })
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class User extends Timestamp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String             username;

    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String             email;

    @Constraints.Required
    @Column(nullable = false)
    public String             password;

    @Column(name = "first_name")
    public String             firstName;
    @Column(name = "last_name")
    public String             lastName;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeUser           type;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Recipe>       recipes;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment>      comments;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Friend>         myFriends;

    @OneToMany(mappedBy = "friend", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Friend>         friends;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Favorite>       recipesFavorites;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Rating>       ratings;

    public User() {
    }

    public User(String username, String email, String password, String firstName, String lastName, TypeUser type) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (!UserDAO.check("email", email, id).isEmpty()) {
            errors.add(new ValidationError("email", "This e-mail is already registered."));
        }
        if (!UserDAO.check("username", username, id).isEmpty()) {
            errors.add(new ValidationError("username", "This username is already registered."));
        }
        return errors.isEmpty() ? null : errors;
    }

    public void prePersistData() throws NoSuchAlgorithmException, InvalidKeySpecException {
        if (firstName != null && firstName.isEmpty()) firstName = null;
        if (lastName != null && lastName.isEmpty()) lastName = null;
        password = Encryptation.createHash(password);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((comments == null) ? 0 : comments.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((friends == null) ? 0 : friends.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((myFriends == null) ? 0 : myFriends.hashCode());
        result = prime * result + ((ratings == null) ? 0 : ratings.hashCode());
        result = prime * result + ((recipes == null) ? 0 : recipes.hashCode());
        result = prime * result + ((recipesFavorites == null) ? 0 : recipesFavorites.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        if (comments == null) {
            if (other.comments != null) return false;
        } else if (!comments.equals(other.comments)) return false;
        if (email == null) {
            if (other.email != null) return false;
        } else if (!email.equals(other.email)) return false;
        if (firstName == null) {
            if (other.firstName != null) return false;
        } else if (!firstName.equals(other.firstName)) return false;
        if (friends == null) {
            if (other.friends != null) return false;
        } else if (!friends.equals(other.friends)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (lastName == null) {
            if (other.lastName != null) return false;
        } else if (!lastName.equals(other.lastName)) return false;
        if (myFriends == null) {
            if (other.myFriends != null) return false;
        } else if (!myFriends.equals(other.myFriends)) return false;
        if (ratings == null) {
            if (other.ratings != null) return false;
        } else if (!ratings.equals(other.ratings)) return false;
        if (recipes == null) {
            if (other.recipes != null) return false;
        } else if (!recipes.equals(other.recipes)) return false;
        if (recipesFavorites == null) {
            if (other.recipesFavorites != null) return false;
        } else if (!recipesFavorites.equals(other.recipesFavorites)) return false;
        if (type != other.type) return false;
        if (username == null) {
            if (other.username != null) return false;
        } else if (!username.equals(other.username)) return false;
        return true;
    }

}
