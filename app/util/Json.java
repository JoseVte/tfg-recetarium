package util;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import play.mvc.Http.Response;
import play.mvc.Result;

public class Json {
    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();
    private static volatile ObjectMapper objectMapper = null;
    
    public static ObjectMapper mapper() {
        if (objectMapper == null) {
            return defaultObjectMapper;
        }
        return objectMapper;
    }
    
    public static ObjectNode generateJsonErrorMessages(String msg) {
        return mapper().createObjectNode().put("error", msg);
    }
    
    public static ObjectNode generateJsonInfoMessages(String msg) {
        return mapper().createObjectNode().put("msg", msg);
    }
    
    @SuppressWarnings("deprecation")
    public static ObjectNode generateJsonPaginateObject(List<? extends Model> models, Long count, Integer page, Integer size, String[] routes) {
        ObjectNode object = mapper().createObjectNode();
        object.put("data", play.libs.Json.toJson(models));
        object.put("total", count);
        if (page > 1) object.put("link-prev", routes[0]);
        if (page * size < count) object.put("link-next", routes[1]);
        object.put("link-self", routes[2]);
        return object;
    }
    
    /**
     * Add the content-type json to response
     *
     * @param response 
     * @param Result httpResponse
     *
     * @return Result
     */
    public static Result jsonResult(Response response, Result httpResponse) {
        response.setContentType("application/json; charset=utf-8");
        return httpResponse;
    }
}
