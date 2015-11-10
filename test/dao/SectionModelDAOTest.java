package dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
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
import models.dao.SectionDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class SectionModelDAOTest extends WithApplication {

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
    public void testDAOFindSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionDAO.find(1);
                assertEquals(section.text, "test");
                assertEquals(section.recipes.size(), 1);
            });
        });
    }

    @Test
    public void testDAONotFoundSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionDAO.find(0);
                assertNull(section);
            });
        });
    }

    @Test
    public void testDAOFindAllSections() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Section> sections = SectionDAO.all();
                long count = SectionDAO.count();
                assertEquals(count, 2);

                assertEquals(sections.get(0).text, "test");
            });
        });
    }

    @Test
    public void testDAOPageSections() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Section> sections = SectionDAO.paginate(0, 1);
                assertEquals(sections.get(0).text, "test");
                assertEquals(sections.size(), 1);

                sections = SectionDAO.paginate(1, 1);
                assertEquals(sections.size(), 1);
            });
        });
    }

    @Test
    public void testDAOCreateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section create = new Section("test2");
                Section section = SectionDAO.create(create);
                assertEquals(section, create);
            });
        });
    }

    @Test
    public void testDAOUpdateSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionDAO.find(1);
                section.text = "Update test";
                Section update = SectionDAO.update(section);
                assertEquals(update.text, "Update test");
            });
        });
    }

    @Test
    public void testDAODeleteSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionDAO.find(1);
                long count = SectionDAO.count();
                assertEquals(count, 2);

                SectionDAO.delete(section);

                count = SectionDAO.count();
                assertEquals(count, 1);
            });
        });
    }

    @Test(expected = RuntimeException.class)
    public void testDAODeleteNotFoundSection() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Section section = SectionDAO.find(0);

                SectionDAO.delete(section);
            });
        });
    }
}
