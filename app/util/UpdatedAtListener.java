package util;

import java.util.Date;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class UpdatedAtListener {
    @PrePersist
    @PreUpdate
    public void setUpdatedAt(final Updatable entity) {
        entity.setUpdatedAt(new Date());
    }
}