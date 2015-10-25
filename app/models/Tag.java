package models;

import java.util.Date;
import java.util.List;

import javax.persistence.*;

import util.*;

@Entity
@EntityListeners({
        CreatedAtListener.class,
        UpdatedAtListener.class
})
@Table(name = "tags")
public class Tag implements Creatable, Updatable {
	@Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    public Integer id;

    @Column(nullable = false)
    public String text;
    
    @ManyToMany(mappedBy="tags")
    public List<Recipe> recipes;
    
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
    
    public Tag() {}
    public Tag(String text) {
    	this.text = text;
    }
}
