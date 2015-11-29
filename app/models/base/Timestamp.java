package models.base;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonProperty;

@EntityListeners({ TimestampListener.class })
@MappedSuperclass
public class Timestamp {
    @Column(name = "created_at")
    @JsonProperty(value = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    public void setCreatedAt(Date date) {
        this.createdAt = date;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    @Column(name = "updated_at")
    @JsonProperty(value = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public void setUpdatedAt(Date date) {
        this.updatedAt = date;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}
