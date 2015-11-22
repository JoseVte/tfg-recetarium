package models.base;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class TimestampListener {

    @PrePersist
    public void onPrePersist(final Timestamp entity) {
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
    }

    @PreUpdate
    public void onPreUpdate(final Timestamp entity) {
        entity.setUpdatedAt(new Date());
    }
}
