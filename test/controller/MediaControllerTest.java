package controller;

import org.junit.Test;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static play.test.Helpers.*;

public class MediaControllerTest extends AbstractTest {
    public MediaControllerTest() {
    }

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
}
