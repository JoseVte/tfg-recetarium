package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import models.manytomany.RecipeTags;
import play.data.validation.Constraints;
import util.Timestamp;
import util.TimestampListener;

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

    @OneToMany(mappedBy = "tag", fetch = FetchType.LAZY, orphanRemoval = true)
    public List<RecipeTags>   recipes          = new ArrayList<RecipeTags>();

    public Tag() {
    }

    public Tag(String text) {
        this.text = text;
    }

    public void prePersistData() {
    }
}
