package util;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.node.ObjectNode;

import models.User;
import models.base.Model;
import models.manytomany.RecipeTags;
import play.Play;
import play.mvc.Http.Response;
import play.mvc.Result;

public class Json {
    private static final ObjectMapper    defaultObjectMapper = new ObjectMapper();
    private static volatile ObjectMapper objectMapper        = null;

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

    @SuppressWarnings("deprecation")
    public static ObjectNode generateJsonPaginateObject(List<? extends Model> models, Long count, Integer page,
            Integer size, String[] routes) {
        ObjectNode object = mapper().createObjectNode();
        object.put("data", play.libs.Json.toJson(models));
        object.put("total", count);
        if (page > 1) object.put("link-prev", routes[0]);
        if (page * size < count) object.put("link-next", routes[1]);
        object.put("link-self", routes[2]);
        return object;
    }

    /**
     * Add the content-type json to response
     *
     * @param response
     * @param Result httpResponse
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
     * @param subject String
     *
     * @return String
     */
    public static String createJwt(String subject) throws JoseException {
        String keySecret = Play.application().configuration().getString("play.crypto.secret");
        Key key = new HmacKey(keySecret.getBytes());

        JwtClaims claims = new JwtClaims();
        claims.setExpirationTimeMinutesInTheFuture(60); // time when the token
                                                        // will expire (60
                                                        // minutes from now)
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setNotBeforeMinutesInThePast(2); // time before which the token
                                                // is not yet valid (2 minutes
                                                // ago)
        claims.setSubject(subject);

        JsonWebSignature jws = new JsonWebSignature();
        jws.setPayload(claims.toJson());
        jws.setKey(key);
        jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.HMAC_SHA256);
        String jwt = jws.getCompactSerialization();

        return jwt;
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

        JwtConsumer jwtConsumer = new JwtConsumerBuilder().setRequireExpirationTime() // the
                                                                                      // JWT
                                                                                      // must
                                                                                      // have
                                                                                      // an
                                                                                      // expiration
                                                                                      // time
                .setAllowedClockSkewInSeconds(30) // allow some leeway in
                                                  // validating time based
                                                  // claims to account for clock
                                                  // skew
                .setRequireSubject().setVerificationKey(key).build();

        try {
            // Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
            return jwtClaims.getSubject();
        } catch (InvalidJwtException | MalformedClaimException e) {
            return null;
        }
    }

}
