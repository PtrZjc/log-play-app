package pl.zajacp.shared;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.Map;

public class RestUtils {

    public static APIGatewayProxyResponseEvent getValidationFailedResponseEvent(Map<String, String> validationErrors) throws JsonProcessingException {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setStatusCode(400);
        var jsonErrors = ObjMapper.INSTANCE.get().writeValueAsString(Map.of("errors", validationErrors));
        response.setBody(jsonErrors);
        return response;
    }

}
