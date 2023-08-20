package pl.zajacp.rest;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RequestParamValidator {

    public static String PARAMETER_MISSING = "Parameter is missing";
    public static String INVALID_INTEGER = "Not a valid integer number";

    public static Map<String, String> validateParameters(APIGatewayProxyRequestEvent event, List<RequiredParam> requiredParams)  {
        Map<String, String> errors = new HashMap<>();

        for (RequiredParam param : requiredParams) {

            var queryParams = Optional.ofNullable(event.getQueryStringParameters()).orElse(Collections.emptyMap());
            var pathParams = Optional.ofNullable(event.getPathParameters()).orElse(Collections.emptyMap());

            String actualValue = switch (param.type) {
                case QUERY -> queryParams.get(param.name);
                case PATH -> pathParams.get(param.name);
            };

            if (actualValue == null) {
                errors.put(param.name, PARAMETER_MISSING);
            } else if (param.dataType == DataType.INTEGER && !actualValue.matches("\\d+")) {
                errors.put(param.name, INVALID_INTEGER);
            }
        }
        return errors;
    }

    public enum ParamType {
        QUERY, PATH
    }


    public enum DataType {
        STRING, INTEGER
    }

    public record RequiredParam(String name, ParamType type, DataType dataType) {
    }
}
