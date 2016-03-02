package util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.manytomany.Favorite;

import java.io.IOException;
import java.util.List;

public class RecipeFavoritesSerializer extends JsonSerializer<List<Favorite>> {

    @Override
    public void serialize(List<Favorite> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (Favorite fav : value) {
            jgen.writeObject(fav.user.id);
        }
        jgen.writeEndArray();
    }
}
