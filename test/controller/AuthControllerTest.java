package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AuthController;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class AuthControllerTest extends AbstractTest {
    private ObjectNode loginJson;
    private ObjectNode badJson;
    private ObjectNode notActive;
    private ObjectNode registerJson;
    private ObjectNode badRegisterJson1;
    private ObjectNode badRegisterJson2;
    private ObjectNode badRegisterJson3;
    private ObjectNode badRegisterJson4;
    private ObjectNode badRegisterJson5;
    private ObjectNode badRegisterJson6;
    private ObjectNode badRegisterJson7;
    private ObjectNode badRegisterJson8;
    private ObjectNode profileOk;

    public AuthControllerTest() {
        loginJson = Json.newObject();
        loginJson.put("email", "test@testing.dev");
        loginJson.put("password", "password");

        badJson = Json.newObject();
        badJson.put("email", "test@testing.dev");
        badJson.put("password", "passwor1");

        notActive = Json.newObject();
        notActive.put("email", "newtest@test.dev");
        notActive.put("password", "password");

        registerJson = Json.newObject();
        registerJson.put("username", "Yasuo");
        registerJson.put("password", "password");
        registerJson.put("password_repeat", "password");
        registerJson.put("email", "newtest@test.dev");

        badRegisterJson1 = Json.newObject();
        badRegisterJson1.put("username", "");
        badRegisterJson1.put("password", "password");
        badRegisterJson1.put("password_repeat", "password");
        badRegisterJson1.put("email", "newtest@test.dev");

        badRegisterJson2 = Json.newObject();
        badRegisterJson2.put("username", "test");
        badRegisterJson2.put("password", "password");
        badRegisterJson2.put("password_repeat", "password");
        badRegisterJson2.put("email", "newtest@test.dev");

        badRegisterJson3 = Json.newObject();
        badRegisterJson3.put("username", "Yasuo");
        badRegisterJson3.put("password", "");
        badRegisterJson3.put("password_repeat", "password");
        badRegisterJson3.put("email", "newtest@test.dev");

        badRegisterJson4 = Json.newObject();
        badRegisterJson4.put("username", "Yasuo");
        badRegisterJson4.put("password", "password");
        badRegisterJson4.put("password_repeat", "");
        badRegisterJson4.put("email", "newtest@test.dev");

        badRegisterJson5 = Json.newObject();
        badRegisterJson5.put("username", "Yasuo");
        badRegisterJson5.put("password", "password");
        badRegisterJson5.put("password_repeat", "password2");
        badRegisterJson5.put("email", "newtest@test.dev");

        badRegisterJson6 = Json.newObject();
        badRegisterJson6.put("username", "Yasuo");
        badRegisterJson6.put("password", "password");
        badRegisterJson6.put("password_repeat", "password");
        badRegisterJson6.put("email", "");

        badRegisterJson7 = Json.newObject();
        badRegisterJson7.put("username", "Yasuo");
        badRegisterJson7.put("password", "password");
        badRegisterJson7.put("password_repeat", "password");
        badRegisterJson7.put("email", "newtest");

        badRegisterJson8 = Json.newObject();
        badRegisterJson8.put("username", "Yasuo");
        badRegisterJson8.put("password", "password");
        badRegisterJson8.put("password_repeat", "password");
        badRegisterJson8.put("email", "test@testing.dev");

        profileOk = Json.newObject();
        profileOk.put("first_name", "Test");
    }

    @Test
    public void testAuthControllerLoginOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertNotNull(responseJson.get(AuthController.AUTH_TOKEN));

            successTest();
        });
    }

    @Test
    public void testAuthControllerLoginFail() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/login").post(badJson).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testAuthControllerLoginNotActive() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(registerJson).get(30000);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            response = WS.url("http://localhost:3333/auth/login").post(notActive).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(registerJson).get(30000);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));
            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson1).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson2).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("username").get(0).asText(), "This username is already registered");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson3).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("password").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson4).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("password_repeat").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson5).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("password").get(0).asText(), "The passwords must be equals");
            assertEquals(responseJson.get("password_repeat").get(0).asText(), "The passwords must be equals");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson6).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson7).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "Valid email required");

            successTest();
        });
    }

    @Test
    public void testAuthControllerRegisterBadRequest8() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/auth/register").post(badRegisterJson8).get(timeout);
            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("email").get(0).asText(), "This e-mail is already registered");

            successTest();
        });
    }

    @Test
    public void testUserControllerProfileOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/profile").setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("username").asText(), "test");

            successTest();
        });
    }

    @Test
    public void testUserControllerUpdateProfileOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/profile").setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(profileOk).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("first_name").asText(), "Test");

            successTest();
        });
    }
}
