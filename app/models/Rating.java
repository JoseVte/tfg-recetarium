package models;

import java.io.Serializable;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name="ratings", uniqueConstraints={@UniqueConstraint(columnNames={"user_id","recipe_id"})})
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Rating implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;
	
	@ManyToOne
	@JoinColumn(name="user_id")
	public User user;
	
	@ManyToOne
	@JoinColumn(name="recipe_id")
	public Recipe recipe;
	
	@Column(precision=10, scale=2)
	public Double rating = 0.0;
	
	public Rating() {}
	public Rating(Double rating) {
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rating other = (Rating) obj;
		if (rating == null) {
			if (other.rating != null)
				return false;
		} else if (!rating.equals(other.rating))
			return false;
		if (recipe == null) {
			if (other.recipe != null)
				return false;
		} else if (!recipe.equals(other.recipe))
			return false;
		if (user == null) {
			if (other.user != null)
				return false;
		} else if (!user.equals(other.user))
			return false;
		return true;
	}

}
