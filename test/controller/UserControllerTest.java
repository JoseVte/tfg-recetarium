package controller;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.TypeUser;

import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import play.test.FakeApplication;
import play.test.WithApplication;
import util.InitDataLoader;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

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

    public UserControllerTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        dataOk = Json.newObject();
        dataOk.put("username", "Yasuo");
        dataOk.put("password", "password");
        dataOk.put("email", "test@test.dev");
        dataOk.put("type", TypeUser.COMUN.toString());
        dataOk.set("recipes", mapper.readTree("[]"));
        dataOk.set("comments", mapper.readTree("[]"));
        dataOk.set("myFriends", mapper.readTree("[]"));
        dataOk.set("friends", mapper.readTree("[]"));
        dataOk.set("recipesFavorites", mapper.readTree("[]"));
        dataOk.set("ratings", mapper.readTree("[]"));

        dataError1 = Json.newObject();
        dataError1.put("username", "");

        dataError2 = Json.newObject();
        dataError2.put("username", "Yasuo");
        dataError2.put("email", "");

        dataError3 = Json.newObject();
        dataError3.put("username", "Yasuo");
        dataError3.put("email", "test@test.dev");
        dataError3.put("password", "");

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
    public void testFindUser() {
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
        });
    }

    @Test
    public void testFindUserNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/5").get().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");
        });
    }

    @Test
    public void testPageUsers() {
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
        });
    }

    @Test
    public void testCreateUser() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataOk).get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("username").asText(), "Yasuo");
        });
    }

    @Test
    public void testCreateUserBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError1).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testCreateUserBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError2).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testCreateUserBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError3).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("password").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testCreateUserBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError4).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("type").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testCreateUserBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError5).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This username is already registered.");
        });
    }

    @Test
    public void testCreateUserBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError6).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This e-mail is already registered.");
        });
    }

    @Test
    public void testCreateUserBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").post(dataError7).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("type").get(0).asText(), "Invalid value");
        });
    }

    @Test
    public void testUpdateUser() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").put(dataOk.put("id", 1)).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("username").asText(), "Yasuo");
        });
    }

    @Test
    public void testUpdateUserBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").put(dataError1.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testUpdateUserBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users").put(dataError2.put("id", 2)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testDeleteUser() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/1").delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "Deleted 1");
        });
    }

    @Test
    public void testDeleteUserNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeData();
            WSResponse response = WS.url("http://localhost:3333/users/5").delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");
        });
    }
}
