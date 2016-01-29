package controller;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;
import util.AbstractTest;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class TagControllerTest extends AbstractTest {
    public TagControllerTest() {
    }

    @Test
    public void testTagControllerSearch() {
        running(testServer(3333, fakeApplication(inMemoryDatabase())), () -> {
            initializeDataController();
            WSResponse response = WS.url("http://localhost:3333/tags?search=tes").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isArray());
            assertEquals(responseJson.size(), 1);
            assertEquals(responseJson.get(0).get("id").asInt(), 1);
            assertEquals(responseJson.get(0).get("text").asText(), "test");

            response = WS.url("http://localhost:3333/tags?search=no-exist").get().get(timeout);
            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            responseJson = response.asJson();
            assertTrue(responseJson.isArray());
            assertEquals(responseJson.size(), 0);

            successTest();
        });
    }
}
