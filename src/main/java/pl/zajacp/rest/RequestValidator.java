package pl.zajacp.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestValidator {

    public static Optional<APIGatewayProxyResponseEvent> validateParameters(APIGatewayProxyRequestEvent event, Map<String, ParameterInfo> requiredParams) {
        Map<String, String> errors = new HashMap<>();

        for (Map.Entry<String, ParameterInfo> entry : requiredParams.entrySet()) {
            String paramName = entry.getKey();
            ParameterInfo paramInfo = entry.getValue();

            String actualValue = switch (paramInfo.type) {
                case QUERY -> event.getQueryStringParameters().get(paramName);
                case PATH -> event.getPathParameters().get(paramName);
            };

            if (actualValue == null) {
                errors.put(paramName, "Parameter is missing");
            } else if (paramInfo.dataType == DataType.NUMBER && !actualValue.matches("\\d+")) {
                errors.put(paramName, "Not a valid number");
            }
        }

        if (!errors.isEmpty()) {
            APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
            response.setStatusCode(400);
            response.setBody("Errors: " + errors);
            return Optional.of(response);
        }
        return Optional.empty();
    }

    public enum ParamType {
        QUERY, PATH
    }


    public enum DataType {
        STRING, NUMBER
    }

    public record ParameterInfo(ParamType type, DataType dataType) {
    }
}
