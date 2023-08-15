package pl.zajacp.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.SneakyThrows;
import pl.zajacp.shared.ObjMapper;

import java.util.Map;
import java.util.Optional;

public class RestCommons {

    public static final String USER_HEADER = "userName";
    public static final Map<String, String> DEFAULT_HEADERS = Map.of("Content-Type", "application/json");

    public static final String SERVER_ERROR_MESSAGE = "Internal Server Error";
    public static final String UNSUPPORTED_JSON_ERROR_MESSAGE = "Unsupported json input obtained";

    public static APIGatewayProxyResponseEvent getResponseEvent() {
        var response = new APIGatewayProxyResponseEvent();
        response.setHeaders(DEFAULT_HEADERS);
        return response;
    }

    public static APIGatewayProxyResponseEvent getValidationFailedResponseEvent(Map<String, String> validationErrors) throws JsonProcessingException {
        var response = getResponseEvent();
        var jsonErrors = ObjMapper.INSTANCE.get().writeValueAsString(Map.of("validationErrors", validationErrors));
        response.setStatusCode(400);
        response.setBody(jsonErrors);
        return response;
    }

    public static String getUserFromHeader(APIGatewayProxyRequestEvent requestEvent, String fallbackUser) {
        var headers = Optional.ofNullable(requestEvent.getHeaders()).orElse(Map.of());
        var userFromHeader = headers.get(USER_HEADER);
        return userFromHeader != null ? userFromHeader : fallbackUser;
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
