package pl.zajacp.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import pl.zajacp.shared.ObjMapper;

import java.util.Map;
import java.util.Optional;

public class RestCommons {

    public static final String USER_HEADER = "User-Name";
    public static final String API_KEY_HEADER = "Api-Key";
    public final static String API_KEY_ENV = "API_KEY";

    public static final Map<String, String> DEFAULT_HEADERS = Map.of("Content-Type", "application/json");

    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";
    public static final String UNSUPPORTED_JSON_ERROR_MESSAGE = "Unsupported json input obtained";

    public static APIGatewayProxyResponseEvent getResponseEvent() {
        var response = new APIGatewayProxyResponseEvent();
        response.setHeaders(DEFAULT_HEADERS);
        return response;
    }

    public static Optional<String> getHeaderValue(APIGatewayProxyRequestEvent requestEvent, String headerKey) {
        return Optional.ofNullable(requestEvent.getHeaders()).map(headers -> headers.get(headerKey));
    }

    public static boolean apiKeysMatch(APIGatewayProxyRequestEvent requestEvent) {
        Optional<String> userApiKey = getHeaderValue(requestEvent, API_KEY_HEADER);
        return userApiKey.isPresent() && userApiKey.get().equals(System.getenv(API_KEY_ENV));
    }

    public static APIGatewayProxyResponseEvent getValidationFailedResponseEvent(Map<String, String> validationErrors) throws JsonProcessingException {
        var response = getResponseEvent();
        var jsonErrors = ObjMapper.INSTANCE.get().writeValueAsString(Map.of("validationErrors", validationErrors));
        response.setStatusCode(400);
        response.setBody(jsonErrors);
        return response;
    }

    public static APIGatewayProxyResponseEvent getUnauthorizedResponseEvent() throws JsonProcessingException {
        var response = getResponseEvent();
        response.setStatusCode(403);
        response.setBody(asErrorJson("'Api-Key' header does not match)"));
        return response;
    }

    @SneakyThrows
    public static String asErrorJson(String errorCause) {
        return ObjMapper.INSTANCE.get().writeValueAsString(Map.of("errorCause", errorCause));
    }

    @SneakyThrows
    public static String asErrorJson(String errorCause, Exception exception) {
        return ObjMapper.INSTANCE.get().writeValueAsString(Map.of(
                "errorCause", errorCause,
                "details", exception.getMessage()
        ));
    }
}
