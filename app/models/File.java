package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.base.Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@JsonPropertyOrder({"id", "url", "type", "title", "new_title", "user", "created_at", "updated_at"})
@Table(name = "files")
public class File extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    public String url;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String title;

    @Column(name = "new_title", nullable = false)
    @JsonProperty(value = "new_title")
    public String newTitle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    public File() {}

    public File(String url, String type, String title, String newTitle, User user) {
        this.url = url;
        this.type = type;
        this.title = title;
        this.newTitle = newTitle;
        this.user = user;
    }

    @Override
    public void handleRelations(Model old) {
        File model = (File) old;
        this.setCreatedAt(model.getCreatedAt());
    }
}
