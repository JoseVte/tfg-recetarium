package models;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import models.base.Model;
import models.dao.UserDAO;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;
import util.Encryptation;

@Entity
@Table(name = "users")
@JsonPropertyOrder({ "id", "username", "email", "password", "first_name", "last_name", "type", "createdAt",
        "updatedAt" })
public class User extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String             username;

    @Constraints.Required
    @Constraints.Email
    @Column(nullable = false, unique = true)
    public String             email;

    @Column(nullable = false)
    public String             password;

    @Column(name = "first_name")
    @JsonProperty(value = "first_name")
    public String             firstName;
    @Column(name = "last_name")
    @JsonProperty(value = "last_name")
    public String             lastName;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeUser           type;
    
    @Column(name = "lost_pass_token")
    @JsonIgnore
    public String lostPassToken;
    
    @Column(name = "lost_pass_expire")
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    public Date lostPassExpire;

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

    @Transient
    private UserDAO           dao;
    
    @Transient
    private boolean updatePassword = true;

    public User() {
        dao = new UserDAO();
    }

    public User(String username, String email, String password, String firstName, String lastName, TypeUser type) {
        dao = new UserDAO();
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public List<ValidationError> validate() {
        List<ValidationError> errors = new ArrayList<ValidationError>();
        if (id != null && dao.find(id) == null) {
            errors.add(new ValidationError("id", "This user doesn't exist"));
        }
        if (!dao.check("email", email, id).isEmpty()) {
            errors.add(new ValidationError("email", "This e-mail is already registered"));
        }
        if (!dao.check("username", username, id).isEmpty()) {
            errors.add(new ValidationError("username", "This username is already registered"));
        }
        if ((id == null || dao.find(id) == null) && (password == null || password.isEmpty())) {
            errors.add(new ValidationError("password", "This field is required"));
        }
        return errors.isEmpty() ? null : errors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#prePersistData()
     */
    @Override
    public void prePersistData() {
        if (firstName != null && firstName.isEmpty()) firstName = null;
        if (lastName != null && lastName.isEmpty()) lastName = null;
        if (password != null && !password.isEmpty() && updatePassword) {
            try {
                password = Encryptation.createHash(password);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see util.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        User user = ((User) old);
        if (password == null || password.isEmpty()){
            this.updatePassword = user.password.equals(password);
            this.password = user.password;
        }
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
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
                + ", firstName=" + firstName + ", lastName=" + lastName + ", type=" + type.toString() + ", recipes="
                + recipes.size() + ", comments=" + comments.size() + ", myFriends=" + myFriends.size() + ", friends="
                + friends.size() + ", recipesFavorites=" + recipesFavorites.size() + ", ratings=" + ratings.size() + 
                ", lostPassToken=" + lostPassToken + ", lostPassExpire=" + lostPassExpire + "]";
    }
}
