package controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import controllers.AuthController;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class FileControllerTest extends AbstractTest {
    private ObjectNode loginJson;
    private ObjectNode loginAdmin;

    public FileControllerTest() {
        loginJson = Json.newObject();
        loginJson.put("email", "test@testing.dev");
        loginJson.put("password", "password");

        loginAdmin = Json.newObject();
        loginAdmin.put("email", "admin@admin.dev");
        loginAdmin.put("password", "password");
    }

    @Test
    public void testFileControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/users/1/files").post(new File("LICENSE")).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/files/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/files/test").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerGetOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/1").get().get(timeout);
            assertEquals(OK, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/files/test").get().get(timeout);
            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerGetNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/2").get().get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/files/not-found").get().get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerDeleteByIdOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);
            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerDeleteByFileOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/test").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);
            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerDeleteByIdAdminOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/1").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);
            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerDeleteByFileAdminOk() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginAdmin).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/test").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);
            assertEquals(OK, response.getStatus());

            successTest();
        });
    }

    @Test
    public void testFileControllerDeleteNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse login = WS.url("http://localhost:3333/auth/login").post(loginJson).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN_FIELD).asText();
            WSResponse response = WS.url("http://localhost:3333/users/1/files/2").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());
            response = WS.url("http://localhost:3333/users/1/files/not-found").setHeader(AuthController.AUTH_TOKEN_HEADER, token).delete().get(timeout);
            assertEquals(NOT_FOUND, response.getStatus());

            successTest();
        });
    }
}
