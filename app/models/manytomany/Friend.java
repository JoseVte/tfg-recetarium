package models.manytomany;

import java.io.Serializable;

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

import models.User;

@Entity
@Table(name = "friends", uniqueConstraints = { @UniqueConstraint(columnNames = { "user_id", "friend_id" }) })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Friend implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    public User               user;

    @ManyToOne
    @JoinColumn(name = "friend_id")
    public User               friend;

    public Friend() {
    }

    public Friend(User user, User friend) {
        this.user = user;
        this.friend = friend;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id);
        result = prime * result + ((friend == null) ? 0 : friend.hashCode());
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Friend other = (Friend) obj;
        if (friend == null) {
            if (other.friend != null) return false;
        } else if (!friend.equals(other.friend)) return false;
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
        return "Friend [id=" + id + ", user=" + user.id + ", friend=" + friend.id + "]";
    }
}
