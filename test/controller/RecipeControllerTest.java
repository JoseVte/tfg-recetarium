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

public class RecipeControllerTest extends AbstractTest {
    private ObjectNode dataOk;
    private ObjectNode dataError1;
    private ObjectNode dataError2;
    private ObjectNode dataError3;
    private ObjectNode dataError4;
    private ObjectNode dataError5;
    private ObjectNode dataError6;
    private ObjectNode dataError7;
    private ObjectNode dataError8;
    private ObjectNode loginJson;

    public RecipeControllerTest() throws Exception {
        dataOk = Json.newObject();
        dataOk.put("title", "New recipe");
        dataOk.put("slug", "new-recipe");
        dataOk.put("category_id", 1);
        dataOk.put("difficulty", "EASY");
        dataOk.put("duration", "00:10:00");

        dataError1 = Json.newObject();
        dataError1.put("title", "");
        dataError1.put("slug", "new-recipe");
        dataError1.put("difficulty", "EASY");
        dataError1.put("duration", "00:10:00");

        dataError2 = Json.newObject();
        dataError2.put("title", "New recipe");
        dataError2.put("slug", "");
        dataError2.put("difficulty", "EASY");
        dataError2.put("duration", "00:10:00");

        dataError3 = Json.newObject();
        dataError3.put("title", "New recipe");
        dataError3.put("slug", "new-recipe");
        dataError3.put("category_id", 0);
        dataError3.put("difficulty", "EASY");
        dataError3.put("duration", "00:10:00");

        dataError4 = Json.newObject();
        dataError4.put("title", "New recipe");
        dataError4.put("slug", "test-2");
        dataError4.put("category_id", 1);
        dataError4.put("difficulty", "EASY");
        dataError4.put("duration", "00:10:00");

        dataError5 = Json.newObject();
        dataError5.put("title", "New recipe");
        dataError5.put("slug", "new-recipe");
        dataError5.put("category_id", 1);
        dataError5.put("duration", "00:10:00");

        dataError6 = Json.newObject();
        dataError6.put("title", "New recipe");
        dataError6.put("slug", "new-recipe");
        dataError6.put("category_id", 1);
        dataError6.put("difficulty", "EASY");

        dataError7 = Json.newObject();
        dataError7.put("title", "New recipe");
        dataError7.put("slug", "new-recipe");
        dataError7.put("category_id", 1);
        dataError7.put("difficulty", "EASY");
        dataError7.put("duration", "asdf");

        dataError8 = Json.newObject();
        dataError8.put("title", "New recipe");
        dataError8.put("slug", "new-recipe");
        dataError8.put("category_id", 1);
        dataError8.put("difficulty", "asdf");
        dataError8.put("duration", "00:10:00");

        loginJson = Json.newObject();
        loginJson.put("email", "test@testing.dev");
        loginJson.put("password", "josevte1");
    }

    @Test
    public void testUserControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/recipes").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/recipes/1").put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/recipes/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerFindAnRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/recipes/test").get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("title").asText(), "Test");
            assertEquals(responseJson.get("slug").asText(), "test");
            assertEquals(responseJson.get("steps").asText(), "Description test");
            assertEquals(responseJson.get("user").get("id").intValue(), 1);
            assertEquals(responseJson.get("category").get("id").intValue(), 1);

            successTest();
        });
    }

    @Test
    public void testRecipeControllerFindAnRecipeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/recipes/not-found").get().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found not-found");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerPageRecipesOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes?page=1&size=1")
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
    public void testRecipeControllerSearchRecipesOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes?page=1&size=1&search=test2")
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

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("title").asText(), "New recipe");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError1).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("title").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError2).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError3).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("category").get(0).asText(), "The category doesn't exist");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError4).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "This slug is already used");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError5).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError6).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError7).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "Invalid value");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest8() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError8).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "Invalid value");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 1)).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("title").asText(), "New recipe");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeOkRequestAdmin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            loginJson = Json.newObject();
            loginJson.put("email", "admin@admin.dev");
            loginJson.put("password", "josevte1");
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 1)).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("title").asText(), "New recipe");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError1.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("title").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError2.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError3.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("category").get(0).asText(), "The category doesn't exist");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError4.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "This slug is already used");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError5.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError6.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "This field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError7.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "Invalid value");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest8() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError8.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "Invalid value");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequestInvalidID() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/2")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "The IDs don't coincide");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataOk.put("id", 5)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").get(0).asText(), "This recipe doesn't exist");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "Deleted 1");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteRecipeOkRequestAdmin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            loginJson = Json.newObject();
            loginJson.put("email", "admin@admin.dev");
            loginJson.put("password", "josevte1");
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "Deleted 1");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteRecipeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");

            successTest();
        });
    }
}
