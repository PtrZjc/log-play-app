package pl.zajacp.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import pl.zajacp.shared.ObjMapper;

import java.net.http.HttpHeaders;
import java.util.Map;
import java.util.Optional;

public class RestCommons {

    public static final String USER_HEADER = "User-Name";
    public static final String API_KEY_HEADER = "X-Api-Key";
    public final static String API_KEY_ENV = "API_KEY";

    public static final Map<String, String> DEFAULT_HEADERS = Map.of(
            "Content-Type", "application/json",
            "Access-Control-Allow-Origin", "*",
            "Access-Control-Allow-Headers", "Content-Type,X-Amz-Date,Authorization,X-Api-Key"
    );

    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";
    public static final String UNSUPPORTED_JSON_ERROR_MESSAGE = "Unsupported json input obtained";
    public static final String DATABASE_ERROR = "Database error";
    public static final String INVALID_API_KEY = "'X-Api-Key' header does not match";

    public static APIGatewayProxyResponseEvent getResponseEvent() {
        return new APIGatewayProxyResponseEvent().withHeaders(DEFAULT_HEADERS);
    }

    public static Optional<String> getHeaderValue(APIGatewayProxyRequestEvent requestEvent, String headerKey) {
        return Optional.ofNullable(requestEvent.getHeaders()).map(headers -> headers.get(headerKey));
    }

    public static APIGatewayProxyResponseEvent getValidationFailedResponseEvent(Map<String, String> validationErrors) throws JsonProcessingException {
        var response = getResponseEvent();
        var jsonErrors = ObjMapper.INSTANCE.get().writeValueAsString(Map.of("validationErrors", validationErrors));
        response.setStatusCode(400);
        response.setBody(jsonErrors);
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
                "exception", exception.getClass().getSimpleName(),
                "details", exception.getMessage()
        ));
    }
}
