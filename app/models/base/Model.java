package models.base;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import models.User;

@MappedSuperclass
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public abstract class Model extends Timestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer                     id;

    @Transient
    protected CrudDAO<? extends Model> dao;

    public Model() {
    }

    /**
     * Fix the data before store
     */
    public void prePersistData() {
    }

    /**
     * Fix the data after store
     * @param create TODO
     */
    public void postPersistData(boolean create) {
    }

    /**
     * Fix the relations between models
     *
     * @param old
     */
    public abstract void handleRelations(Model old);

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof User)) return false;
        User other = (User) obj;
        if (id == null) {
            if (other.id != null) return false;
        } else if (!id.equals(other.id)) return false;
        return true;
    }
}
