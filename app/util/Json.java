package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.base.Model;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.keys.AesKey;
import org.jose4j.keys.HmacKey;
import org.jose4j.lang.JoseException;
import play.Play;
import play.mvc.Http.Response;
import play.mvc.Result;

import java.security.Key;
import java.util.List;

public class Json {
    private static final ObjectMapper defaultObjectMapper = new ObjectMapper();
    private static final ObjectMapper objectMapper = null;

    private static ObjectMapper mapper() {
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

    public static ObjectNode generateJsonBooleanInfoMessages(String field, boolean value) {
        return mapper().createObjectNode().put(field, value);
    }

    @SuppressWarnings("deprecation")
    public static ObjectNode generateJsonPaginateObject(List<? extends Model> models, Long count, Integer page, Integer size, String[] routes, boolean search) {
        ObjectNode object = mapper().createObjectNode();
        object.put("data", play.libs.Json.toJson(models));
        object.put("total", count);
        if (page > 1) object.put("link-prev", routes[0]);
        if (page * size < models.size() && search) object.put("link-next", routes[1]);
        else if (page * size < count && !search) object.put("link-next", routes[1]);
        object.put("link-self", routes[2]);
        return object;
    }

    /**
     * Add the content-type json to response
     *
     * @param response     Response
     * @param httpResponse Result
     *
     * @return Result
     */
    public static Result jsonResult(Response response, Result httpResponse) {
        response.setContentType("application/json; charset=utf-8");
        return httpResponse;
    }

    /**
     * Create a token for the user
     *
     * @param subject       String
     * @param setExpiration boolean
     *
     * @return String
     */
    public static String createJwt(String subject) throws JoseException {
        String keySecret = Play.application().configuration().getString("play.crypto.secret");
        int expiration = Play.application().configuration().getInt("jwt.expiry.minutes");
        Key key = new HmacKey(keySecret.getBytes());

        JwtClaims claims = new JwtClaims();
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2);
        claims.setSubject(subject);
        claims.setExpirationTimeMinutesInTheFuture(expiration);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);

        return jws.getCompactSerialization();
    }

    /**
     * Check the auth token
     *
     * @param jwt String
     *
     * @return String
     */
    public static String checkJwt(String jwt) {
        String keySecret = Play.application().configuration().getString("play.crypto.secret");
        Key key = new AesKey(keySecret.getBytes());

        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setAllowedClockSkewInSeconds(30).setRequireSubject().setVerificationKey(key).build();

        try {
            // Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
            return jwtClaims.getSubject();
        } catch (InvalidJwtException | MalformedClaimException e) {
            return null;
        }
    }

}
