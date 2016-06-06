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

public class CategoryControllerTest extends AbstractTest {
    private ObjectNode loginJson;
    private ObjectNode loginAdmin;
    private ObjectNode dataOk;
    private ObjectNode dataError1;

    public CategoryControllerTest() {
        dataOk = Json.newObject();
        dataOk.put("text", "New category");

        dataError1 = Json.newObject();
        dataError1.put("text", "");

        loginJson = Json.newObject();
        loginJson.put("email", "test@testing.dev");
        loginJson.put("password", "password");

        loginAdmin = Json.newObject();
        loginAdmin.put("email", "admin@admin.dev");
        loginAdmin.put("password", "password");
    }

    @Test
    public void testCategoryControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/categories").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/categories/1").put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/categories/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/categories?ids=1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();

            response = WS.url("http://localhost:3333/categories").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/categories/1").put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/categories/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/categories?ids=1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testCategoryControllerAll() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/categories").setHeader("Accept-Pagination", "false").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isArray());
            assertEquals(responseJson.size(), 2);
            assertEquals(responseJson.get(0).get("id").asInt(), 1);
            assertEquals(responseJson.get(0).get("text").asText(), "test");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerAllPaginated() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/categories?page=1&size=1").setHeader("Accept-Pagination", "true").get().get(timeout);
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
    public void testCategoryControllerFindACategoryOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/categories/1").get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("text").asText(), "test");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerFindACategoryNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/categories/5").get().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the category 5");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerCreateCategoryOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("text").asText(), "New category");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerCreateCategoryBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError1).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("text").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerUpdateCategoryOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).patch( dataOk.put("id", 1)).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("text").asText(), "New category");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerUpdateCategoryBadRequestInvalidID() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories/2")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "the IDs must be equals");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerUpdateCategoryNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 5)).get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the category 5");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerDeleteCategoryOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "deleted the category <strong>1</strong>");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerDeleteCategoryNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the category 5");

            successTest();
        });
    }

    @Test
    public void testCategoryControllerDeleteMultipleCategoryOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/categories?ids=1&ids=2&ids=3")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "deleted 2 categories");

            successTest();
        });
    }
}
