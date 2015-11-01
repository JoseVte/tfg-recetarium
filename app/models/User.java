package models;

import util.*;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.dao.UserDAO;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners({
        CreatedAtListener.class,
        UpdatedAtListener.class
})
@Table(name = "users")
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User implements Creatable, Updatable, Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String username;

    @Constraints.Required
    @Column(nullable = false, unique = true)
    public String email;

    @Constraints.Required
    @Column(nullable = false)
    public String password;

    @Column(name="first_name")
    public String firstName;
    @Column(name="last_name")
    public String lastName;

    @Constraints.Required
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public TypeUser type;

    @OneToMany(mappedBy="user")
    public List<Recipe> recipes;
    
    @OneToMany(mappedBy="user")
    public List<Comment> comments;
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="friends",
    	joinColumns={@JoinColumn(name="user_id")},
    	inverseJoinColumns={@JoinColumn(name="friend_id")},
    	uniqueConstraints={@UniqueConstraint(columnNames={"user_id","friend_id"})})
    public List<User> myFriends;
    
    @ManyToMany(mappedBy="myFriends")
    public List<User> friends;
    
    @ManyToMany(fetch=FetchType.LAZY)
    @JoinTable(name="favorites",
    	joinColumns={@JoinColumn(name="user_id")},
    	inverseJoinColumns={@JoinColumn(name="recipe_id")},
    	uniqueConstraints={@UniqueConstraint(columnNames={"user_id","recipe_id"})})
    public List<Recipe> recipesFavorites;
    
    @OneToMany(mappedBy="user",fetch=FetchType.LAZY)
    public List<Rating> ratings;

    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @Override
    public void setCreatedAt(Date date) {
        this.createdAt = date;
    }

    @Override
    public void setUpdatedAt(Date date) {
        this.updatedAt = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public User() {}

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

	public void emptyToNull() {
		if (firstName != null && firstName.isEmpty()) firstName = null;
		if (lastName != null && lastName.isEmpty()) lastName = null;
	}
}
