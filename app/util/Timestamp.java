package util;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@MappedSuperclass
public class Timestamp {
    @Column(name="created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    
    public void setCreatedAt(Date date) {
        this.createdAt = date;
    }
    
    public Date getCreatedAt() {
        return createdAt;
    }
    

    @Column(name="updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    public void setUpdatedAt(Date date) {
        this.updatedAt = date;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }
}