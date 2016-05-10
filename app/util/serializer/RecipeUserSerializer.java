package util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.User;

import java.io.IOException;

public class RecipeUserSerializer extends JsonSerializer<User> {

    @Override
    public void serialize(User value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeNumberField("id", value.id);
        jgen.writeStringField("username", value.username);
        jgen.writeStringField("email", value.email);
        jgen.writeStringField("first_name", value.firstName);
        jgen.writeStringField("last_name", value.lastName);
        if (value.avatar != null) {
            jgen.writeObjectFieldStart("avatar");
            jgen.writeNumberField("id", value.avatar.id);
            jgen.writeStringField("new_title", value.avatar.newTitle);
            jgen.writeEndObject();
        }
        jgen.writeEndObject();
    }
}
