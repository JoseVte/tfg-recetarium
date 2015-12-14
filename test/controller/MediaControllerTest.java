package controller;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.UNAUTHORIZED;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.inMemoryDatabase;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

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
            WSResponse response = WS.url("http://localhost:3333/media/1").post(new String()).get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());
            response = WS.url("http://localhost:3333/media/1").delete().get(timeout);
            assertEquals(UNAUTHORIZED, response.getStatus());

            successTest();
        });
    }
}
