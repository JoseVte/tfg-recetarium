package util;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class Model extends Timestamp{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Integer            id;
    
    public Model() {}
    
    /**
     * Fix the data before store
     */
    public abstract void prePersistData();
    
    /**
     * Fix the relations between models
     *
     * @param old
     */
    public abstract void handleRelations(Model old);
}
