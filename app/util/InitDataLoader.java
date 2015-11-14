package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

public class InitDataLoader {
    private static final Logger log = LoggerFactory.getLogger(InitDataLoader.class);

    public static void load(EntityManager entityManager, String fileNameInClasspath) throws IOException {
        Yaml yaml = new Yaml();
        InputStream input = new FileInputStream(new File(fileNameInClasspath));
        for (Object obj : yaml.loadAll(input)) {
            persist(entityManager, obj);
        }

        log.debug("Init data loaded");
    }

    private static void persist(EntityManager entityManager, Object obj) {
        if (obj instanceof Collection) {
            for (Object object : (Collection<?>) obj) {
                persist(entityManager, object);
            }

        } else if (obj instanceof Map) {
            for (Object object : ((Map<?, ?>) obj).values()) {
                persist(entityManager, object);
            }

        } else {
            entityManager.persist(obj);
            log.trace("saved: {}", obj);
        }
    }
    
    public static void initializeData() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("memoryPersistenceUnit");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {

            // Start the transaction
            trx.begin();
            load(em, "test/init-data.yml");
            // Commit and end the transaction
            trx.commit();
        } catch (RuntimeException | IOException e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
            }
        } finally {
            // Close the manager
            em.close();
            emf.close();
        }
    }
}
