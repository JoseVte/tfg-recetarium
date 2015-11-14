package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

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
}
