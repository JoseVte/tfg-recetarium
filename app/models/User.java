package models;

import util.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners({
        CreatedAtListener.class,
        UpdatedAtListener.class
})
@Table(name = "users")
public class User implements Creatable, Updatable {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

    @Column(nullable = false)
    public String username;

    @Column(nullable = false)
    public String email;

    @Column(nullable = false)
    public String password;

    @Column(name="first_name")
    public String firstName;
    @Column(name="last_name")
    public String lastName;

    @Enumerated(EnumType.STRING)
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

    @Column(name="created_at", insertable=false, nullable=false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name="updated_at", insertable=false, nullable=false)
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
}
