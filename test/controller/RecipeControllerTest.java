package controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AuthController;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

import java.util.Arrays;

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
    private ObjectNode dataCommentOk;
    private ObjectNode loginJson;
    private ObjectNode loginAdmin;
    private ObjectNode ingredientsCreate;
    private ObjectNode ingredientsCreate2;
    private ObjectNode ingredientsUpdate;
    private ObjectNode ingredientsUpdate2;

    public RecipeControllerTest() throws Exception {
        ingredientsCreate = Json.newObject();
        ingredientsCreate.put("name", "test");
        ingredientsCreate.put("count", 1);
        ingredientsCreate2 = Json.newObject();
        ingredientsCreate2.put("name", "test 2");
        ingredientsCreate2.put("count", 2);


        ingredientsUpdate = Json.newObject();
        ingredientsUpdate.put("id", 2);
        ingredientsUpdate.put("name", "new test");
        ingredientsUpdate.put("count", 3);
        ingredientsUpdate2 = Json.newObject();
        ingredientsUpdate2.put("id", 3);
        ingredientsUpdate2.put("name", "new test 2");
        ingredientsUpdate2.put("count", 4);

        dataOk = Json.newObject();
        dataOk.put("title", "New recipe");
        dataOk.put("slug", "new-recipe");
        dataOk.put("category_id", 1);
        dataOk.put("difficulty", "EASY");
        dataOk.put("duration", "00:10:00");
        dataOk.put("visibility", "PUBLIC");

        dataError1 = Json.newObject();
        dataError1.put("title", "");
        dataError1.put("slug", "new-recipe");
        dataError1.put("difficulty", "EASY");
        dataError1.put("duration", "00:10:00");
        dataError1.put("visibility", "PUBLIC");

        dataError2 = Json.newObject();
        dataError2.put("title", "New recipe");
        dataError2.put("slug", "");
        dataError2.put("difficulty", "EASY");
        dataError2.put("duration", "00:10:00");
        dataError2.put("visibility", "PUBLIC");

        dataError3 = Json.newObject();
        dataError3.put("title", "New recipe");
        dataError3.put("slug", "new-recipe");
        dataError3.put("category_id", 0);
        dataError3.put("difficulty", "EASY");
        dataError3.put("duration", "00:10:00");
        dataError3.put("visibility", "PUBLIC");

        dataError4 = Json.newObject();
        dataError4.put("title", "New recipe");
        dataError4.put("slug", "test-2");
        dataError4.put("category_id", 1);
        dataError4.put("difficulty", "EASY");
        dataError4.put("duration", "00:10:00");
        dataError4.put("visibility", "PUBLIC");

        dataError5 = Json.newObject();
        dataError5.put("title", "New recipe");
        dataError5.put("slug", "new-recipe");
        dataError5.put("category_id", 1);
        dataError5.put("duration", "00:10:00");
        dataError5.put("visibility", "PUBLIC");

        dataError6 = Json.newObject();
        dataError6.put("title", "New recipe");
        dataError6.put("slug", "new-recipe");
        dataError6.put("category_id", 1);
        dataError6.put("difficulty", "EASY");
        dataError6.put("visibility", "PUBLIC");

        dataError7 = Json.newObject();
        dataError7.put("title", "New recipe");
        dataError7.put("slug", "new-recipe");
        dataError7.put("category_id", 1);
        dataError7.put("difficulty", "EASY");
        dataError7.put("duration", "asdf");
        dataError7.put("visibility", "PUBLIC");

        dataError8 = Json.newObject();
        dataError8.put("title", "New recipe");
        dataError8.put("slug", "new-recipe");
        dataError8.put("category_id", 1);
        dataError8.put("difficulty", "asdf");
        dataError8.put("duration", "00:10:00");
        dataError8.put("visibility", "PUBLIC");

        dataCommentOk = Json.newObject();
        dataCommentOk.put("text", "Comment test");

        loginJson = Json.newObject();
        loginJson.put("email", "test@testing.dev");
        loginJson.put("password", "password");

        loginAdmin = Json.newObject();
        loginAdmin.put("email", "admin@admin.dev");
        loginAdmin.put("password", "password");
    }

    @Test
    public void testRecipeControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/recipes").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/recipes/1").put(dataOk.put("id", 1)).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/recipes/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            response = WS.url("http://localhost:3333/recipes/1/comments").post(dataOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/recipes/1/comments/1").put(dataCommentOk).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/recipes/1/comments/1").delete().get(timeout);
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
            assertEquals(responseJson.get("error").asText(), "not found the recipe not-found");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerPageRecipesOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
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
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
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
    public void testRecipeControllerCheckSlugOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/new-slug/check")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCheckSlugBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/test/check")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCheckSlugWithIdOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/test/check/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCheckSlugWithIdBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/test/check/2")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerIsMineOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/test/mine")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerIsMineOkRequestAdmin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/test/mine")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerIsMineNotFoundRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/new-slug/mine")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).head().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());

            successTest();
        });
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRecipeControllerPrivateEvent() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();

            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            dataOk.put("ingredients", Json.toJson(Arrays.asList(ingredientsCreate, ingredientsCreate2)));
            dataOk.put("visibility", "PRIVATE");
            WSResponse response = WS.url("http://localhost:3333/recipes").setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);
            assertEquals(CREATED, response.getStatus());

            response = WS.url("http://localhost:3333/recipes?page=1&size=5").setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);
            JsonNode responseJson = response.asJson();
            assertEquals(responseJson.get("data").size(), 3);
            assertEquals(responseJson.get("total").intValue(), 3);

            response = WS.url("http://localhost:3333/recipes/new-recipe").setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);
            assertEquals(OK, response.getStatus());

            response = WS.url("http://localhost:3333/recipes?page=1&size=5").get().get(timeout);
            responseJson = response.asJson();
            assertEquals(responseJson.get("data").size(), 2);
            assertEquals(responseJson.get("total").intValue(), 2);

            response = WS.url("http://localhost:3333/recipes/new-recipe").get().get(timeout);
            assertEquals(FORBIDDEN, response.getStatus());

            successTest();
        });
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRecipeControllerCreateRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            dataOk.put("ingredients", Json.toJson(Arrays.asList(ingredientsCreate, ingredientsCreate2)));
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataOk).get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("title").asText(), "New recipe");
            assertTrue(responseJson.get("ingredients").isArray());
            assertEquals(responseJson.get("ingredients").size(), 2);

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError1).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("title").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError2).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError3).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("category").get(0).asText(), "the category doesn't exist: 0");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError4).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "this slug is already registered");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError5).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError6).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError7).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "invalid value asdf");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateRecipeBadRequest8() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataError8).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "invalid value");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerAddIngredientOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/ingredient")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(ingredientsCreate).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name").asText(), ingredientsCreate.get("name").asText());
            assertEquals(responseJson.get("count").asText(), ingredientsCreate.get("count").asText());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerAddIngredientBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/ingredient")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(Json.newObject()).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerAddIngredientBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/0/ingredient")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(ingredientsCreate).get(timeout);

            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteIngredientOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/2/ingredient/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteIngredientNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/ingredient/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the ingredient 1");

            successTest();
        });
    }

    @SuppressWarnings("deprecation")
    @Test
    public void testRecipeControllerUpdateRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);

            WSResponse response = WS.url("http://localhost:3333/recipes/1/ingredient")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(ingredientsCreate).get(timeout);
            response = WS.url("http://localhost:3333/recipes/1/ingredient")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(ingredientsCreate2).get(timeout);

            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            dataOk.put("id", 1).put("ingredients", Json.toJson(Arrays.asList(ingredientsUpdate, ingredientsUpdate2)));
            response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).patch(dataOk).get(timeout);

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
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
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
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError1.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("title").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError2.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError3.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("category").get(0).asText(), "the category doesn't exist: 0");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest4() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError4.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("slug").get(0).asText(), "this slug is already registered");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest5() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError5.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest6() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError6.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest7() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError7.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("duration").get(0).asText(), "invalid value asdf");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest8() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataError8.put("id", 1)).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("difficulty").get(0).asText(), "invalid value");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequestInvalidID() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/2")
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
    public void testRecipeControllerUpdateRecipeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/5")
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
    public void testRecipeControllerDeleteRecipeOkRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "deleted the recipe <strong>1</strong>");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteRecipeOkRequestAdmin() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "deleted the recipe <strong>1</strong>");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteRecipeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/5")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the recipe 5");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerGetReplies() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).get().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isArray());
            assertEquals(responseJson.size(), 1);
            assertEquals(responseJson.get(0).get("id").intValue(), 2);

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateCommentOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments").setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataCommentOk).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 3);
            assertEquals(responseJson.get("text").asText(), "Comment test");

            response = WS.url("http://localhost:3333/recipes/1/comments/3").setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataCommentOk).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 4);
            assertEquals(responseJson.get("text").asText(), "Comment test");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateCommentBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments").setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(Json.newObject()).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("text").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerCreateCommentNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/0/comments").setHeader(AuthController.AUTH_TOKEN_HEADER, token).post(dataCommentOk).get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the recipe 0");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateCommentOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataCommentOk).get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("text").asText(), "Comment test");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateCommentBadRequest() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(Json.newObject()).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("text").get(0).asText(), "this field is required");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateCommentBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataCommentOk).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "you can't edit the comment of other user");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateCommentBadRequest3() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/2/comments/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataCommentOk).get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "the IDs must be equals");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateCommentNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/0").setHeader(AuthController.AUTH_TOKEN_HEADER, token).put(dataCommentOk).get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the comment 0");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteCommentOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "deleted the comment <strong>1</strong>");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerDeleteCommentNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/recipes/1/comments/0").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "not found the comment 0");

            successTest();
        });
    }
}
