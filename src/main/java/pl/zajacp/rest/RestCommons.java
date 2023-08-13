package pl.zajacp.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import pl.zajacp.shared.ObjMapper;

import java.util.Map;
import java.util.Optional;

public class RestCommons {

    public static final String USER_HEADER = "userName";

    public static APIGatewayProxyResponseEvent getValidationFailedResponseEvent(Map<String, String> validationErrors) throws JsonProcessingException {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(400);
        var jsonErrors = ObjMapper.INSTANCE.get().writeValueAsString(Map.of("errors", validationErrors));
        response.setBody(jsonErrors);
        return response;
    }

    public static String getUserFromHeader(APIGatewayProxyRequestEvent requestEvent, String fallbackUser) {
        var headers = Optional.ofNullable(requestEvent.getHeaders()).orElse(Map.of());
        var userFromHeader = headers.get(USER_HEADER);
        return userFromHeader != null ? userFromHeader : fallbackUser;
    }
}
