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

public class UserControllerTest extends AbstractTest {
    ObjectNode dataOk;
    ObjectNode dataError1;
    ObjectNode dataError2;
    ObjectNode dataError3;
    ObjectNode dataError4;
    ObjectNode dataError5;
    ObjectNode dataError6;
    ObjectNode dataError7;
    ObjectNode loginJson;

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

        loginJson = Json.newObject();
        loginJson.put("email", "admin@admin.dev");
        loginJson.put("password", "password");
    }

    @Test
    public void testUserControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/users/1").get().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users").get().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1").put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/profile").get().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/profile").put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testUserControllerLoggedButUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            ObjectNode comunUser = Json.newObject();
            comunUser.put("email", "test@testing.dev");
            comunUser.put("password", "password");

            WSResponse login = WS.url("http://localhost:3333/auth/login").post(comunUser).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();

            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token)
                    .put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token)
                    .delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testUserControllerFindAnUserOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("username").asText(), "test");
            assertEquals(responseJson.get("email").asText(), "test@testing.dev");

            successTest();
        });
    }

    @Test
    public void testUserControllerFindAnUserNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the user 5");

            successTest();
        });
    }

    @Test
    public void testUserControllerPageUsersOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users?page=1&size=1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);

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

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("username").asText(), "Yasuo");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError1).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError2).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError3).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("password").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError4).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("type").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError5).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "this username is already registered");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError6).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "this email is already registered");

            successTest();
        });
    }

    @Test
    public void testUserControllerCreateUserBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError7).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("type").get(0).asText(), "invalid value");

            successTest();
        });
    }

    @Test
    public void testUserControllerUpdateUserOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 1)).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("username").asText(), "Yasuo");

            successTest();
        });
    }

    @Test
    public void testUserControllerUpdateUserBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError1.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testUserControllerUpdateUserBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError2.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testUserControllerUpdateUserBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 2)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "the IDs must be equals");

            successTest();
        });
    }

    @Test
    public void testUserControllerUpdateUserNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 5)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").get(0).asText(), "the ID doesn't exist: 5");

            successTest();
        });
    }

    @Test
    public void testUserControllerDeleteUserOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "deleted the user <strong>1</strong>");

            successTest();
        });
    }

    @Test
    public void testUserControllerDeleteUserNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the user 5");

            successTest();
        });
    }
}
