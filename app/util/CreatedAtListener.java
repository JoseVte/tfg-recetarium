package util;

import java.util.Date;

import javax.persistence.PrePersist;

public class CreatedAtListener {

    @PrePersist
    public void setCreatedAt(final Creatable entity) {
        entity.setCreatedAt(new Date());
    }

}