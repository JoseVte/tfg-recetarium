package util.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import models.manytomany.Friend;

import java.io.IOException;
import java.util.List;

public class UserFriendsSerializer extends JsonSerializer<List<Friend>> {

    @Override
    public void serialize(List<Friend> value, JsonGenerator jgen, SerializerProvider provider)throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        for (Friend friend : value) {
            if (friend != null && friend.friend != null && friend.user != null) {
                jgen.writeStartObject();
                jgen.writeNumberField("friend_id", friend.friend.id);
                jgen.writeNumberField("user_id", friend.user.id);
                jgen.writeEndObject();
            }
        }
        jgen.writeEndArray();
    }
}
