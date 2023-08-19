package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.Optional;

import static pl.zajacp.rest.RestCommons.API_KEY_ENV;
import static pl.zajacp.rest.RestCommons.API_KEY_HEADER;
import static pl.zajacp.rest.RestCommons.DATABASE_ERROR;
import static pl.zajacp.rest.RestCommons.INVALID_API_KEY;
import static pl.zajacp.rest.RestCommons.SERVER_ERROR_MESSAGE;
import static pl.zajacp.rest.RestCommons.UNSUPPORTED_JSON_ERROR_MESSAGE;
import static pl.zajacp.rest.RestCommons.asErrorJson;
import static pl.zajacp.rest.RestCommons.getHeaderValue;
import static pl.zajacp.rest.RestCommons.getResponseEvent;

@AllArgsConstructor
public abstract class BaseGameRecordHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected final DynamoDbRepository<GameRecord> gameItemRepository;

    protected abstract APIGatewayProxyResponseEvent handleValidRequestEvent(APIGatewayProxyRequestEvent requestEvent) throws JsonProcessingException;

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        APIGatewayProxyResponseEvent responseEvent;
        try {
            responseEvent = apiKeysMatch(requestEvent)
                    ? handleValidRequestEvent(requestEvent)
                    : getUnauthorizedResponseEvent();
        } catch (JsonProcessingException e) {
            context.getLogger().log("JsonProcessingException occurred: " + e);
            responseEvent = getUnsupportedJsonResponseEvent(e);
        } catch (DynamoDbException e) {
            context.getLogger().log("DynamoDbException occurred: " + e);
            responseEvent = getDatabaseError(e);
        } catch (Exception e) {
            context.getLogger().log("Exception occurred: " + e);
            responseEvent = getServerErrorResponseEvent(e);
        }
        context.getLogger().log("Response code " + responseEvent.getStatusCode());
        return responseEvent;
    }

    private boolean apiKeysMatch(APIGatewayProxyRequestEvent requestEvent) {
        Optional<String> userApiKey = getHeaderValue(requestEvent, API_KEY_HEADER);
        return userApiKey.isPresent() && userApiKey.get().equals(System.getenv(API_KEY_ENV));
    }

    private APIGatewayProxyResponseEvent getUnauthorizedResponseEvent() throws JsonProcessingException {
        return getResponseEvent()
                .withStatusCode(403)
                .withBody(INVALID_API_KEY);
    }

    private static APIGatewayProxyResponseEvent getUnsupportedJsonResponseEvent(JsonProcessingException e) {
        return getResponseEvent()
                .withStatusCode(400)
                .withBody(asErrorJson(UNSUPPORTED_JSON_ERROR_MESSAGE, e));
    }

    private static APIGatewayProxyResponseEvent getDatabaseError(DynamoDbException e) {
        return getResponseEvent()
                .withStatusCode(e.statusCode())
                .withBody(asErrorJson(DATABASE_ERROR, e));
    }

    private static APIGatewayProxyResponseEvent getServerErrorResponseEvent(Exception e) {
        return getResponseEvent()
                .withStatusCode(500)
                .withBody(asErrorJson(SERVER_ERROR_MESSAGE, e));
    }
}
