package models;

import java.io.Serializable;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import play.data.validation.Constraints;
import util.*;

@Entity
@EntityListeners({ TimestampListener.class })
@Table(name = "tags")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Tag extends Timestamp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;

    @Constraints.Required
    @Column(nullable = false)
    public String             text;

    @ManyToMany(mappedBy = "tags")
    public List<Recipe>       recipes;

    public Tag() {
    }

    public Tag(String text) {
        this.text = text;
    }

    public void emptyToNull() {
    }
}
