import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import models.User;
import models.dao.UserDAO;
import play.db.jpa.JPA;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class UserModelTest extends WithApplication {
    
    @Override
    public FakeApplication provideFakeApplication(){
        return fakeApplication(inMemoryDatabase());
    }
    
    public void initializeData() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("memoryPersistenceUnit");
        EntityManager em = emf.createEntityManager();
        EntityTransaction trx = em.getTransaction();
        try {

            //Start the transaction
            trx.begin();
            InitDataLoader.load(em, "test/init-data.yml");
          //Commit and end the transaction
            trx.commit();
        } catch (RuntimeException | IOException e) {
            if (trx != null && trx.isActive()) {
                trx.rollback();
             }
             throw e;
          } finally {
             //Close the manager
             em.close();
             emf.close();
          }
    }

    @Test
    public void testDAOFindUser() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                User user = UserDAO.find(1);
                assertEquals(user.username, "test");
            });
        });
    }
/*
    @Test
    public void testFindEmployeeNotFound() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee e = EmployeeService.find(5);
                assertNull(e);
            });
        });
    }

    @Test
    public void testFindAllEmployees() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Employee> e = EmployeeService.all();
                long count = EmployeeService.count();
                assertEquals(count, 4);

                assertTrue(e.contains(new Employee("Josrom")));
                assertTrue(e.contains(new Employee("Dantar")));
                assertTrue(e.contains(new Employee("Ericmaster")));
                assertTrue(e.contains(new Employee("xChaco")));
            });
        });
    }

    @Test
    public void testPageEmployees() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                List<Employee> e = EmployeeService.paginate(0, 3);

                assertTrue(e.contains(new Employee("Josrom")));
                assertTrue(e.contains(new Employee("Dantar")));
                assertTrue(e.contains(new Employee("Ericmaster")));
                assertFalse(e.contains(new Employee("xChaco")));

                e = EmployeeService.paginate(1, 3);
                assertEquals(e.size(), 1);
            });
        });
    }

    @Test
    public void testCreateEmployee() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);
                assertEquals(e, create);
            });
        });
    }

    @Test
    public void testUpdateEmployee() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);
                e.name = "Update test";
                Employee update = EmployeeService.update(e);
                assertEquals(update.name, "Update test");
            });
        });
    }

    @Test
    public void testDeleteEmployee() {
        running(fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);

                assertTrue(EmployeeService.delete(e.id));
                assertFalse(EmployeeService.delete(e.id));
            });
        });
    }*/
}
