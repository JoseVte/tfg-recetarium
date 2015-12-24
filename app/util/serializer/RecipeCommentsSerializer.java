package util.serializer;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import models.Comment;

public class RecipeCommentsSerializer extends JsonSerializer<List<Comment>> {

    @Override
    public void serialize(List<Comment> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (Comment comment : value) {
            if (comment.parent == null) jgen.writeObject(comment);
            jgen.writeObject(comment);
        }
        jgen.writeEndArray();
    }

}
