package util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.manytomany.RecipeFiles;

import java.io.IOException;
import java.util.List;

public class RecipeFilesSerializer extends JsonSerializer<List<RecipeFiles>> {

    @Override
    public void serialize(List<RecipeFiles> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (RecipeFiles file : value) {
            jgen.writeObject(file.file);
        }
        jgen.writeEndArray();
    }
}
