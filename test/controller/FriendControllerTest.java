package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AuthController;
import models.enums.TypeUser;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class FriendControllerTest extends AbstractTest {
    ObjectNode dataOk;
    ObjectNode dataOkUser;
    ObjectNode loginJson;
    ObjectNode loginAdmin;

    public FriendControllerTest() {
        dataOk = Json.newObject();
        dataOk.put("id", "3");

        dataOkUser = Json.newObject();
        dataOkUser.put("username", "Yasuo");
        dataOkUser.put("password", "password");
        dataOkUser.put("email", "test@test.dev");
        dataOkUser.put("type", TypeUser.COMUN.toString());

        loginJson = Json.newObject();
        loginJson.put("email", "test@testing.dev");
        loginJson.put("password", "password");

        loginAdmin = Json.newObject();
        loginAdmin.put("email", "admin@admin.dev");
        loginAdmin.put("password", "password");
    }

    @Test
    public void testUserControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/users/1/friends").get().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/friends").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/friends/2").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFriendControllerList() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/friends?search=admin")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("data").isArray());
            assertEquals(responseJson.get("data").size(), 1);
            assertEquals(responseJson.get("total").intValue(), 1);
            assertNotNull(responseJson.get("link-self"));
            assertNull(responseJson.get("link-next"));
            assertNull(responseJson.get("link-prev"));

            response = WS.url("http://localhost:3333/users/1/friends?search=no-exist")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("data").isArray());
            assertEquals(responseJson.get("data").size(), 0);

            successTest();
        });
    }

    @Test
    public void testFriendControllerAddOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOkUser).get(timeout);

            login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            response = WS.url("http://localhost:3333/users/1/friends")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("added").asBoolean());

            successTest();
        });
    }

    @Test
    public void testFriendControllerAddBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/users/2/friends")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "The IDs don't coincide");

            successTest();
        });
    }

    @Test
    public void testFriendControllerDeleteOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/friends/2")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("deleted").asBoolean());

            successTest();
        });
    }

    @Test
    public void testFriendControllerDeleteBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/users/3/friends/2")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "The IDs don't coincide");

            successTest();
        });
    }
}
