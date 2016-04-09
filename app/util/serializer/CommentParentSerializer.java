package util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Comment;

import java.io.IOException;

public class CommentParentSerializer extends JsonSerializer<Comment> {

    @Override
    public void serialize(Comment value, JsonGenerator jgen, SerializerProvider provider)throws IOException, JsonProcessingException {
        jgen.writeStartObject();
        jgen.writeObjectField("id", value.id);
        jgen.writeEndObject();
    }
}
