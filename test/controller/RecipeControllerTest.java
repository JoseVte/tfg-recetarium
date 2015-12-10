package controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.CREATED;
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AuthController;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

public class RecipeControllerTest extends AbstractTest {
    int        timeout = 4000;
    ObjectNode dataOk;
    ObjectNode dataError1;
    ObjectNode dataError2;
    ObjectNode dataError3;
    ObjectNode dataError4;
    ObjectNode dataError5;
    ObjectNode dataError6;
    ObjectNode dataError7;
    ObjectNode loginJson;

    @SuppressWarnings("deprecation")
    public RecipeControllerTest() throws Exception {
        ObjectNode idOk = Json.newObject();
        idOk.put("id", 1);
        ObjectNode idError = Json.newObject();
        idError.put("id", 0);

        dataOk = Json.newObject();
        dataOk.put("title", "New recipe");
        dataOk.put("slug", "new-recipe");
        dataOk.put("user", idOk);
        dataOk.put("category", idOk);

        dataError1 = Json.newObject();
        dataError1.put("title", "");
        dataError1.put("slug", "new-recipe");
        dataError1.put("user", idOk);

        dataError2 = Json.newObject();
        dataError2.put("title", "New recipe");
        dataError2.put("slug", "");
        dataError2.put("user", idOk);

        dataError3 = Json.newObject();
        dataError3.put("title", "New recipe");
        dataError3.put("slug", "new-recipe");

        dataError4 = Json.newObject();
        dataError4.put("title", "New recipe");
        dataError4.put("slug", "new-recipe");
        dataError4.put("user", "");

        dataError5 = Json.newObject();
        dataError5.put("title", "New recipe");
        dataError5.put("slug", "new-recipe");
        dataError5.put("user", idError);

        dataError6 = Json.newObject();
        dataError6.put("title", "New recipe");
        dataError6.put("slug", "new-recipe");
        dataError6.put("user", idOk);
        dataError6.put("category", idError);

        dataError7 = Json.newObject();
        dataError7.put("title", "New recipe");
        dataError7.put("slug", "test-2");
        dataError7.put("user", idOk);
        dataError7.put("category", idOk);

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
            assertEquals(responseJson.get("description").asText(), "Description test");
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
            assertEquals(responseJson.get("user").get(0).asText(), "This field is required");

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
            assertEquals(responseJson.get("user").get(0).asText(), "Invalid value");

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
            assertEquals(responseJson.get("user").get(0).asText(), "The user doesn't exist");

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
            assertEquals(responseJson.get("category").get(0).asText(), "The category doesn't exist");

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
            assertEquals(responseJson.get("slug").get(0).asText(), "This slug is already used");

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
            assertEquals(responseJson.get("user").get(0).asText(), "This field is required");

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
            assertEquals(responseJson.get("user").get(0).asText(), "Invalid value");

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
            assertEquals(responseJson.get("user").get(0).asText(), "The user doesn't exist");

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
            assertEquals(responseJson.get("category").get(0).asText(), "The category doesn't exist");

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
            assertEquals(responseJson.get("slug").get(0).asText(), "This slug is already used");

            successTest();
        });
    }

    @Test
    public void testRecipeControllerUpdateRecipeBadRequest8() {
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
