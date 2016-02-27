package models;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import models.base.Model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@JsonPropertyOrder({"id", "url", "type", "title", "user", "created_at", "updated_at"})
@Table(name = "files")
public class File extends Model implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(nullable = false)
    public String url;

    @Column(nullable = false)
    public String type;

    @Column(nullable = false)
    public String title;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    public User user;

    public File() {}

    public File(String url, String type, String title, User user) {
        this.url = url;
        this.type = type;
        this.title = title;
        this.user = user;
    }

    @Override
    public void handleRelations(Model old) {
        File model = (File) old;
        this.setCreatedAt(model.getCreatedAt());
    }
}
