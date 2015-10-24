import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.libs.Json;
import play.libs.ws.WS;
import play.libs.ws.WSResponse;

import static org.junit.Assert.*;
import static play.test.Helpers.*;

public class ControllerTest {
    int timeout = 4000;
    ObjectNode dataOk;
    ObjectNode dataError1;
    ObjectNode dataError2;

    public ControllerTest() {
        dataOk = Json.newObject();
        dataOk.put("name", "Yasuo");

        dataError1 = Json.newObject();
        dataError1.put("name", "");

        dataError2 = Json.newObject();
    }

    public void initializeData() {
        String [] list = {"Josrom", "Dantar", "Ericmaster", "xChaco"};

        for (String name : list) {
            ObjectNode user = Json.newObject();
            user.put("name", name);
            WS.url("http://localhost:3333/employees").post(user).get(timeout);
        }
    }

    @Test
    public void testFindEmployee() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees/1")
                .get()
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("name").asText(), "Josrom");
        });
    }

    @Test
    public void testFindEmployeeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees/5")
                .get()
                .get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");
        });
    }

    @Test
    public void testPageEmployees() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .get()
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertTrue(responseJson.get("data").isArray());
            assertEquals(responseJson.get("data").size(), 3);
            assertEquals(responseJson.get("total").intValue(), 4);
            assertNotNull(responseJson.get("link-self"));
            assertNotNull(responseJson.get("link-next"));
            assertNull(responseJson.get("link-prev"));
        });
    }

    @Test
    public void testCreateEmployee() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .post(dataOk)
                .get(timeout);

            assertEquals(CREATED, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 5);
            assertEquals(responseJson.get("name").asText(), "Yasuo");
        });
    }

    @Test
    public void testCreateEmployeeBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .post(dataError1)
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testCreateEmployeeBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .post(dataError2)
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testUpdateEmployee() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .put(dataOk.put("id", 1))
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("id").intValue(), 1);
            assertEquals(responseJson.get("name").asText(), "Yasuo");
        });
    }

    @Test
    public void testUpdateEmployeeBadRequest1() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .put(dataError1.put("id", 1))
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testUpdateEmployeeBadRequest2() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees")
                .put(dataError2.put("id", 2))
                .get(timeout);

            assertEquals(BAD_REQUEST, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("name").get(0).asText(), "This field is required");
        });
    }

    @Test
    public void testDeleteEmployee() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees/1")
                .delete()
                .get(timeout);

            assertEquals(OK, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("msg").asText(), "Deleted 1");
        });
    }

    @Test
    public void testDeleteEmployeeNotFound() {
        running(testServer(3333, fakeApplication(inMemoryDatabase()) ), () -> {
            initializeData();
            WSResponse response = WS
                .url("http://localhost:3333/employees/5")
                .delete()
                .get(timeout);

            assertEquals(NOT_FOUND, response.getStatus());
            assertEquals("application/json; charset=utf-8", response.getHeader("Content-Type"));

            JsonNode responseJson = response.asJson();
            assertTrue(responseJson.isObject());
            assertEquals(responseJson.get("error").asText(), "Not found 5");
        });
    }
}
