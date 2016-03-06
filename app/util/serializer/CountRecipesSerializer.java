package util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.Recipe;

import java.io.IOException;
import java.util.List;

public class CountRecipesSerializer extends JsonSerializer<List<Recipe>> {

    @Override
    public void serialize(List<Recipe> value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
        jgen.writeNumber(value.size());
    }
}
