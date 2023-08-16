package pl.zajacp.repository;

import pl.zajacp.model.GameRecord;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public enum GamesLogRepository {
    INSTANCE;

    private final DynamoDbRepository<GameRecord> repository;

    private final static String GAMES_LOG_TABLE_ENV = "GAMES_LOG_TABLE";
    private final static String REGION_ENV = "AWS_REGION";
    private final static Region DEFAULT_REGION = Region.EU_CENTRAL_1;

    GamesLogRepository() {
        repository = new DynamoDbRepository<>(
                DynamoDbClient.builder().region(getRegion()).build(),
                getGamesLogTableName(),
                GameRecord.class);
    }

    public DynamoDbRepository<GameRecord> get() {
        return repository;
    }

    private String getGamesLogTableName() {
        String tableName = System.getenv(GAMES_LOG_TABLE_ENV);
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable " + GAMES_LOG_TABLE_ENV + " is not set or is empty.");
        }
        return tableName;
    }

    private Region getRegion() {
        String region = System.getenv(REGION_ENV);
        return region == null ? DEFAULT_REGION : Region.of(region);
    }
}
