import models.Employee;
import models.EmployeeService;
import org.junit.Test;
import play.db.jpa.JPA;

import java.util.List;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class ModelTest {
    public void initializeData() {
        String [] list = {"Josrom", "Dantar", "Ericmaster", "xChaco"};

        for (String name : list) {
            Employee e = new Employee(name);
            EmployeeService.create(e);
        }
    }

    @Test
    public void testFindEmployee() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee e = EmployeeService.find(1);
                assertEquals(e.name, "Josrom");
            });
        });
    }

    @Test
    public void testFindEmployeeNotFound() {
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee e = EmployeeService.find(5);
                assertNull(e);
            });
        });
    }

    @Test
    public void testFindAllEmployees() {
        running (fakeApplication(inMemoryDatabase()), () -> {
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
        running (fakeApplication(inMemoryDatabase()), () -> {
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
        running (fakeApplication(inMemoryDatabase()), () -> {
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
        running (fakeApplication(inMemoryDatabase()), () -> {
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
        running (fakeApplication(inMemoryDatabase()), () -> {
            JPA.withTransaction(() -> {
                initializeData();
                Employee create = new Employee("New test");
                Employee e = EmployeeService.create(create);
                
                assertTrue(EmployeeService.delete(e.id));
                assertFalse(EmployeeService.delete(e.id));
            });
        });
    }
}
