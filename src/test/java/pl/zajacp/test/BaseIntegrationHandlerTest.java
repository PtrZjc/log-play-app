package pl.zajacp.test;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import org.junit.jupiter.api.extension.ExtendWith;
import pl.zajacp.model.GameRecord;
import pl.zajacp.test.database.DynamoDbTest;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

import java.util.Map;

import static pl.zajacp.repository.GameLogRepositoryCommons.TIMESTAMP_RANGE_KEY;
import static pl.zajacp.repository.GameLogRepositoryCommons.USER_HASH_KEY;
import static pl.zajacp.rest.RestCommons.API_KEY_ENV;
import static pl.zajacp.rest.RestCommons.API_KEY_HEADER;
import static pl.zajacp.test.TestData.TEST_API_KEY_VALUE;

@ExtendWith(SystemStubsExtension.class)
@DynamoDbTest(entityClass = GameRecord.class, hashKey = USER_HASH_KEY, rangeKey = TIMESTAMP_RANGE_KEY)
public class BaseIntegrationHandlerTest {

    @SystemStub
    private static EnvironmentVariables testWideVariables = new EnvironmentVariables(API_KEY_ENV, TEST_API_KEY_VALUE);

    protected static APIGatewayProxyRequestEvent getRequestEventWithValidApiKey() {
        return new APIGatewayProxyRequestEvent().withHeaders(Map.of(API_KEY_HEADER, TEST_API_KEY_VALUE));
    }
}
