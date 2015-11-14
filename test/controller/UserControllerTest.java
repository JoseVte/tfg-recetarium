package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.TypeUser;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

public class UserControllerTest extends WithApplication {
    int        timeout = 4000;
    ObjectNode dataOk;
    ObjectNode dataError1;
    ObjectNode dataError2;
    ObjectNode dataError3;
    ObjectNode dataError4;
    ObjectNode dataError5;
    ObjectNode dataError6;
    ObjectNode dataError7;
    
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public UserControllerTest() throws Exception {
        dataOk = Json.newObject();
        dataOk.put("username", "Yasuo");
        dataOk.put("password", "password");
        dataOk.put("email", "test@test.dev");
        dataOk.put("type", TypeUser.COMUN.toString());

        dataError1 = Json.newObject();
        dataError1.put("username", "");
        dataError1.put("email", "test@test.dev");
        dataError1.put("password", "password");
        dataError1.put("type", TypeUser.COMUN.toString());

        dataError2 = Json.newObject();
        dataError2.put("username", "Yasuo");
        dataError2.put("email", "");
        dataError2.put("password", "password");
        dataError2.put("type", TypeUser.COMUN.toString());

        dataError3 = Json.newObject();
        dataError3.put("username", "Yasuo");
        dataError3.put("email", "test@test.dev");
        dataError3.put("password", "");
        dataError3.put("type", TypeUser.COMUN.toString());

        dataError4 = Json.newObject();
        dataError4.put("username", "Yasuo");
        dataError4.put("email", "test@test.dev");
        dataError4.put("password", "password");
        dataError4.put("type", "");

        dataError5 = Json.newObject();
        dataError5.put("username", "test");
        dataError5.put("email", "test@test.dev");
        dataError5.put("password", "password");
        dataError5.put("type", TypeUser.COMUN.toString());

        dataError6 = Json.newObject();
        dataError6.put("username", "test2");
        dataError6.put("email", "test@testing.dev");
        dataError6.put("password", "password");
        dataError6.put("type", TypeUser.COMUN.toString());

        dataError7 = Json.newObject();
        dataError7.put("username", "test2");
        dataError7.put("email", "test@test.dev");
        dataError7.put("password", "password");
        dataError7.put("type", "OTHER");
    }

    @Override
    public FakeApplication provideFakeApplication() {
        return fakeApplication(inMemoryDatabase());
    }

    public void initializeData() {
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
        } finally {
            // Close the manager
            em.close();
            emf.close();
        }
    }

    @Test
    public void testFindUserOkRequest() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/1").get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("username").asText(), "test");
            assertEquals(responseJson.get("email").asText(), "test@testing.dev");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testFindUserNotFound() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/5").get().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testPageUsersOkRequest() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users?page=1&size=1").get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("data").isArray());
            assertEquals(responseJson.get("data").size(), 1);
            assertEquals(responseJson.get("total").intValue(), 2);
            assertNotNull(responseJson.get("link-self"));
            assertNotNull(responseJson.get("link-next"));
            assertNull(responseJson.get("link-prev"));

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserOkRequest() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataOk).get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("username").asText(), "Yasuo");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest1() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError1).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This field is required");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest2() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError2).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This field is required");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest3() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError3).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("password").get(0).asText(), "This field is required");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest4() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError4).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("type").get(0).asText(), "This field is required");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest5() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError5).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This username is already registered");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest6() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError6).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This e-mail is already registered");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testCreateUserBadRequest7() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError7).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("type").get(0).asText(), "Invalid value");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testUpdateUserOkRequest() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/1").put(dataOk.put("id", 1)).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("username").asText(), "Yasuo");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testUpdateUserBadRequest1() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/1").put(dataError1.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This field is required");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testUpdateUserBadRequest2() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/1").put(dataError2.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This field is required");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testUpdateUserNotFound() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/5").put(dataOk.put("id", 5)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").get(0).asText(), "This user doesn't exist");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testDeleteUserOkRequest() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/1").delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "Deleted 1");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }

    @Test
    public void testDeleteUserNotFound() {
        System.out.print(ANSI_YELLOW + "Test Name: " + ANSI_PURPLE + Thread.currentThread().getStackTrace()[1].getMethodName() + ANSI_RESET + "\t\t");
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/5").delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");

            System.out.println("[" + ANSI_GREEN + "success" + ANSI_RESET + "]");
        });
    }
}
