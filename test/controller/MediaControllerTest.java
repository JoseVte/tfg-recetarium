package controller;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import java.io.File;

import org.junit.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

import controllers.AuthController;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

public class MediaControllerTest extends AbstractTest {
    int        timeout = 4000;

    public MediaControllerTest() {}
    
    @Test
    public void testMediaControllerUnauthorized() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/media/1").post(new File("LICENSE")).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/media/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }
    
    @Test
    public void testMediaControllerUpload() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            ObjectNode comunUser = Json.newObject();
            comunUser.put("email", "test@testing.dev");
            comunUser.put("password", "josevte1");

            WSResponse login = WS.url("http://localhost:3333/auth/login").post(comunUser).get(timeout);
            token = login.asJson().get(AuthController.AUTH_TOKEN).asText();
            WSResponse response = WS.url("http://localhost:3333/media/1")
                    .setHeader(AuthController.AUTH_TOKEN_HEADER, token).setBody((new File("LICENSE"))).post(new File("LICENSE")).get(timeout);
            assertEquals(OK, response.getStatus());

            successTest();
        });
    }
}
