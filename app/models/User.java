package models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import controllers.UserController.UserRequest;
import models.base.Model;
import models.enums.TypeUser;
import models.manytomany.Favorite;
import models.manytomany.Friend;
import models.manytomany.Rating;
import models.service.FileService;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import util.Encryptation;
import util.serializer.UserFriendsSerializer;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

@Entity
@Table(name = "users")
@JsonPropertyOrder({"id", "username", "email", "first_name", "last_name", "type", "myFriends", "friends", "created_at", "updated_at"})
public class User extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    public String username;

    @Column(nullable = false, unique = true)
    public String email;

    @JsonIgnore
    @Column(nullable = false)
    public String password;

    @Column(name = "first_name")
    @JsonProperty(value = "first_name")
    public String firstName;
    @Column(name = "last_name")
    @JsonProperty(value = "last_name")
    public String lastName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeUser type;

    @JsonProperty(value = "num_recipes")
    @Column(nullable = false, name = "num_recipes", columnDefinition = "int(5) default '0'")
    public Integer numRecipes = 0;

    @Column(name = "lost_pass_token")
    @JsonIgnore
    public String lostPassToken;

    @Column(name = "lost_pass_expire")
    @JsonIgnore
    @Temporal(TemporalType.TIMESTAMP)
    public Date lostPassExpire;

    @Column(name = "validation_email_token")
    @JsonIgnore
    public String validationEmailToken;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonProperty(value = "avatar")
    @JoinColumn(name = "avatar_id", nullable = true)
    public File avatar;

    public String language = "es";

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Recipe> recipes = new ArrayList<Recipe>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<File> files = new ArrayList<File>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Comment> comments = new ArrayList<Comment>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = UserFriendsSerializer.class)
    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Friend> myFriends = new ArrayList<Friend>();

    @Fetch(value = FetchMode.SUBSELECT)
    @JsonSerialize(using = UserFriendsSerializer.class)
    @OneToMany(mappedBy = "friend", fetch = FetchType.EAGER, orphanRemoval = true)
    public List<Friend> friends = new ArrayList<Friend>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Favorite> recipesFavorites = new ArrayList<Favorite>();

    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<Rating> ratings = new ArrayList<Rating>();

    public User() {
    }

    public User(String username, String email, String password, String firstName, String lastName, TypeUser type) {
        this.username = username;
        this.email = email;
        if (password != null && !password.isEmpty()) this.password = Encryptation.createHash(password);
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }

    public User(UserRequest user) {
        this.id = user.id;
        this.username = user.username;
        this.email = user.email;
        if (user.password != null && !user.password.isEmpty()) this.password = Encryptation.createHash(user.password);
        this.firstName = user.first_name;
        this.lastName = user.last_name;
        this.type = user.type;
        this.language = user.language;
        if (user.avatar != null) this.avatar = FileService.find(this, user.avatar);
    }

    public User(LinkedHashMap map) {
        this.id = Integer.valueOf(map.get("id").toString());
        this.username = map.get("username").toString();
        this.email = map.get("email").toString();

        if (map.get("first_name") != null) this.firstName = map.get("first_name").toString();
        if (map.get("last_name") != null) this.lastName = map.get("last_name").toString();
        if (map.get("language") != null) this.language = map.get("language").toString();
        this.type = TypeUser.valueOf(map.get("type").toString());
        this.numRecipes = Integer.valueOf(map.get("num_recipes").toString());

        this.setCreatedAt(new Date(Long.valueOf(map.get("created_at").toString())));
        this.setUpdatedAt(new Date(Long.valueOf(map.get("updated_at").toString())));
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
        if (language != null && language.isEmpty()) language = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see util.Model#handleRelations(util.Model old)
     */
    @Override
    public void handleRelations(Model old) {
        User user = ((User) old);
        if (password == null || password.isEmpty()) {
            this.password = user.password;
        }
        if (type == null) {
            this.type = user.type;
        }
        this.setCreatedAt(user.getCreatedAt());
        this.numRecipes = user.numRecipes;
        this.recipes = user.recipes;
        this.files = user.files;
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
                + recipes.size() + ", files=" + files.size() + ", comments=" + comments.size() + ", myFriends="
                + myFriends.size() + ", friends=" + friends.size() + ", recipesFavorites=" + recipesFavorites.size()
                + ", ratings=" + ratings.size() + ", lostPassToken=" + lostPassToken + ", lostPassExpire=" + lostPassExpire + "]";
    }

    /**
     * Check if user is an admin
     *
     * @return boolean
     */
    @JsonIgnore
    public boolean isAdmin() {
        return type.equals(TypeUser.ADMIN);
    }

    @JsonIgnore
    public boolean isActive() {
        return this.validationEmailToken == null;
    }

    @JsonIgnore
    public String getFullName() {
        String name = "";
        if (firstName != null) {
            name+= firstName;
            if (lastName != null) {
                name += " " + lastName;
            }
        } else {
            name = username;
        }
        return name;
    }

    @JsonIgnore
    public static String Search(String search) {
        return "(users.username LIKE '%" + search + "%' OR users.email LIKE '%" + search + "%' OR users.firstName LIKE '%" + search + "%' OR users.lastName LIKE '%" + search + "%')";
    }
}
