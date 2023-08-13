package pl.zajacp;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static pl.zajacp.rest.RequestParamValidator.DataType.INTEGER;
import static pl.zajacp.rest.RequestParamValidator.DataType.STRING;
import static pl.zajacp.rest.RequestParamValidator.INVALID_INTEGER;
import static pl.zajacp.rest.RequestParamValidator.PARAMETER_MISSING;
import static pl.zajacp.rest.RequestParamValidator.ParamType.PATH;
import static pl.zajacp.rest.RequestParamValidator.ParamType.QUERY;
import static pl.zajacp.rest.RequestParamValidator.RequiredParam;
import static pl.zajacp.rest.RequestParamValidator.validateParameters;
import static pl.zajacp.test.domain.ValidationResultAssertion.assertThat;

public class RequestParamValidatorTest {

    private APIGatewayProxyRequestEvent event;

    private final static String QUERY_STRING_PARAM = "QueryStringParam";
    private final static String QUERY_INTEGER_PARAM = "QueryIntegerParam";
    private final static String PATH_STRING_PARAM = "PathStringParam";
    private final static String PATH_INTEGER_PARAM = "PathIntegerParam";


    private static final List<RequiredParam> REQUIRED_PARAMS = List.of(
            new RequiredParam(QUERY_STRING_PARAM, QUERY, STRING),
            new RequiredParam(QUERY_INTEGER_PARAM, QUERY, INTEGER),
            new RequiredParam(PATH_STRING_PARAM, PATH, STRING),
            new RequiredParam(PATH_INTEGER_PARAM, PATH, INTEGER)
    );

    @BeforeEach
    public void setUp() {
        event = new APIGatewayProxyRequestEvent();
    }

    @Test
    public void shouldPassOnProperParameters() {
        //given
        event.withQueryStringParameters(Map.of(
                QUERY_STRING_PARAM, "string",
                QUERY_INTEGER_PARAM, "1"));
        event.withPathParameters(Map.of(
                PATH_STRING_PARAM, "string",
                PATH_INTEGER_PARAM, "1"
        ));

        //when
        Map<String, String> validationResult = validateParameters(event, REQUIRED_PARAMS);

        //then
        assertThat(validationResult)
                .hasNoErrors();
    }

    @Test
    public void shouldReturnErrorsForImproperParameters() {
        //given
        event.withQueryStringParameters(Map.of(
                QUERY_INTEGER_PARAM, "string"));
        event.withPathParameters(Map.of(
                PATH_INTEGER_PARAM, "1.0"
        ));

        //when
        Map<String, String> errors = validateParameters(event, REQUIRED_PARAMS);

        //then
        assertThat(errors)
                .hasErrorQuantityOf(4)
                .hasErrorOnProperty(QUERY_STRING_PARAM).withDetails(PARAMETER_MISSING)
                .hasErrorOnProperty(QUERY_INTEGER_PARAM).withDetails(INVALID_INTEGER)
                .hasErrorOnProperty(PATH_STRING_PARAM).withDetails(PARAMETER_MISSING)
                .hasErrorOnProperty(PATH_INTEGER_PARAM).withDetails(INVALID_INTEGER);
    }
}
