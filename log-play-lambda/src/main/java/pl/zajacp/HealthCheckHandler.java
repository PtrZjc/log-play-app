package pl.zajacp;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import pl.zajacp.model.GameRecord;
import pl.zajacp.repository.DynamoDbRepository;
import pl.zajacp.repository.GamesLogRepository;
import pl.zajacp.shared.ObjMapper;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DescribeTableResponse;

import java.util.Map;

import static pl.zajacp.rest.RestCommons.getResponseEvent;

public class HealthCheckHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDbRepository<GameRecord> gameItemRepository;

    public HealthCheckHandler() {
        this.gameItemRepository = GamesLogRepository.INSTANCE.get();
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        try {
            DescribeTableRequest describeTableRequest = DescribeTableRequest.builder()
                    .tableName(gameItemRepository.getTableName()).build();

            DescribeTableResponse describeTableResponse = gameItemRepository.getClient().describeTable(describeTableRequest);
            String healthCheckResponse = ObjMapper.INSTANCE.get().writeValueAsString(Map.of(
                    "healthCheckSuccessful", true,
                    "describeTable", describeTableResponse.toString()
            ));
            return getResponseEvent()
                    .withStatusCode(200)
                    .withBody(healthCheckResponse);
        } catch (Exception e) {
            String failedResponse;
            try {
                failedResponse = ObjMapper.INSTANCE.get().writeValueAsString(Map.of(
                        "healthCheckSuccessful", false,
                        "details", e.getMessage(),
                        "location", e.getStackTrace()[0]
                ));
            } catch (JsonProcessingException ex) {
                failedResponse = e.getMessage();
            }
            return getResponseEvent()
                    .withStatusCode(200)
                    .withBody(failedResponse);
        }
    }
}
