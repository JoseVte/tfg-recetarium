package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Map;

import javax.persistence.EntityManager;

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
}
