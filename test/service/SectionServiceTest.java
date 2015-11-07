package service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import models.Section;
import models.service.SectionService;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class SectionServiceTest extends WithApplication {

    @Override
    public FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    public void initializeData() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("memoryPersistenceUnit");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {

            // Start the transaction
            trx.begin();
            InitDataLoader.load(em, "test/init-data.yml");
            // Commit and end the transaction
            trx.commit();
        } catch (RuntimeException | IOException e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
            }
            throw e;
        } finally {
            // Close the manager
            em.close();
            emf.close();
        }
    }

    @Test
    public void testServiceFindSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionService.find(1);
                assertEquals(section.text, "test");
                assertEquals(section.recipes.size(), 1);
            });
        });
    }

    @Test
    public void testServiceNotFoundSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionService.find(0);
                assertNull(section);
            });
        });
    }

    @Test
    public void testServiceFindAllSections() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Section> sections = SectionService.all();
                long count = SectionService.count();
                assertEquals(count, 2);

                assertEquals(sections.get(0).text, "test");
            });
        });
    }

    @Test
    public void testServicePageSections() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Section> sections = SectionService.paginate(0, 1);
                assertEquals(sections.get(0).text, "test");
                assertEquals(sections.size(), 1);

                sections = SectionService.paginate(1, 1);
                assertEquals(sections.size(), 1);
            });
        });
    }

    @Test
    public void testServiceCreateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section create = new Section("test2");
                Section section = SectionService.create(create);
                assertEquals(section, create);
            });
        });
    }

    @Test
    public void testServiceUpdateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionService.find(1);
                section.text = "Update test";
                Section update = SectionService.update(section);
                assertEquals(update.text, "Update test");
            });
        });
    }

    @Test
    public void testServiceDeleteSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                long count = SectionService.count();
                assertEquals(count, 2);

                assertTrue(SectionService.delete(1));

                count = SectionService.count();
                assertEquals(count, 1);
            });
        });
    }

    public void testServiceDeleteNotFoundSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                assertFalse(SectionService.delete(0));
            });
        });
    }
}
