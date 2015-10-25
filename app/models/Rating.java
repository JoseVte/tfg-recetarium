package models;

import java.io.Serializable;

import javax.persistence.*;

@Entity
@Table(name="ratings")
public class Rating implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@ManyToOne
	@JoinColumn(name="user_id")
	public User user;
	
	@Id
	@ManyToOne
	@JoinColumn(name="recipe_id")
	public Recipe recipe;
	
	@Column(precision=10, scale=2)
	public Double rating = 0.0;
}
