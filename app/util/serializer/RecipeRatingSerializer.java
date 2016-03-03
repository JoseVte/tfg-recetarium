package util.serializer;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import models.manytomany.Rating;

public class RecipeRatingSerializer extends JsonSerializer<List<Rating>> {

    @Override
    public void serialize(List<Rating> value, JsonGenerator jgen, SerializerProvider provider)
            throws IOException, JsonProcessingException {
        Double ratingValue = 0.0;
        Map<Integer, Double> ratings = new HashMap<Integer, Double>();
        for (Rating rating : value) {
            ratingValue += rating.rating;
            ratings.put(rating.user.id, rating.rating);
        }
        if (value.size() >= 1) ratingValue = ratingValue / value.size();
        jgen.writeObjectField("rating", ratingValue);
        jgen.writeObjectField("ratings", ratings);
        jgen.writeEndObject();
    }
}
