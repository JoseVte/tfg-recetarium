package models;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import models.dao.UserDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Encryptation;
import util.Model;
import util.Timestamp;
import util.TimestampListener;

@Entity
@Table(name = "users")
public class User extends Model implements Serializable {
    private static final long serialVersionUID = 1L;
    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String             username;

    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String             email;

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

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Recipe>       recipes          = new ArrayList<Recipe>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment>      comments         = new ArrayList<Comment>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Friend>       myFriends        = new ArrayList<Friend>();

    @JsonIgnore
    @OneToMany(mappedBy = "friend", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Friend>       friends          = new ArrayList<Friend>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Favorite>     recipesFavorites = new ArrayList<Favorite>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Rating>       ratings          = new ArrayList<Rating>();

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
        if (id != null && UserDAO.find(id) == null) {
            errors.add(new ValidationError("id", "This user doesn't exist"));
        }
        if (!UserDAO.check("email", email, id).isEmpty()) {
            errors.add(new ValidationError("email", "This e-mail is already registered"));
        }
        if (!UserDAO.check("username", username, id).isEmpty()) {
            errors.add(new ValidationError("username", "This username is already registered"));
        }
        if ((id == null || UserDAO.find(id) == null) && (password == null || password.isEmpty())) {
            errors.add(new ValidationError("password", "This field is required"));
        }
        return errors.isEmpty() ? null : errors;
    }

    public void prePersistData(){
        if (firstName != null && firstName.isEmpty()) firstName = null;
        if (lastName != null && lastName.isEmpty()) lastName = null;
        try {
            password = Encryptation.createHash(password);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
    }
    
    public void handleRelations(Model old) {
        User user = ((User)old);
        if (password != null && password.isEmpty()) this.password = user.password;
        this.setCreatedAt(user.getCreatedAt());
        this.recipes = user.recipes;
        this.myFriends = user.myFriends;
        this.friends = user.friends;
        this.recipesFavorites = user.recipesFavorites;
        this.ratings = user.ratings;
        this.comments = user.comments;
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
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((friends == null) ? 0 : friends.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
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
        if (email == null) {
            if (other.email != null) return false;
        } else if (!email.equals(other.email)) return false;
        if (firstName == null) {
            if (other.firstName != null) return false;
        } else if (!firstName.equals(other.firstName)) return false;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        if (lastName == null) {
            if (other.lastName != null) return false;
        } else if (!lastName.equals(other.lastName)) return false;
        if (username == null) {
            if (other.username != null) return false;
        } else if (!username.equals(other.username)) return false;
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
                + ", firstName=" + firstName + ", lastName=" + lastName + ", type=" + type.toString() + ", recipes="
                + recipes.size() + ", comments=" + comments.size() + ", myFriends=" + myFriends.size() + ", friends="
                + friends.size() + ", recipesFavorites=" + recipesFavorites.size() + ", ratings=" + ratings.size()
                + "]";
    }
}
