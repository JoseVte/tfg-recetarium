package models;

import util.*;

import javax.persistence.*;
import java.util.Date;

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

    public String firstName;
    public String lastName;

    @Enumerated(EnumType.STRING)
    public TypeUser type;

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

    public User(Integer id, String username, String email, String password, String firstName, String lastName, TypeUser type) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.type = type;
    }
}
