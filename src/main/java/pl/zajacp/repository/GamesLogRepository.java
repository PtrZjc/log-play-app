package pl.zajacp.repository;

import pl.zajacp.model.GameRecord;

public enum GamesLogRepository {
    INSTANCE;

    private final DynamoDbRepository<GameRecord> repository;

    private final static String GAMES_LOG_TABLE_ENV = "GAMES_LOG_TABLE";

    GamesLogRepository() {
        repository = new DynamoDbRepository<>(getGamesLogTableName(), GameRecord.class);
    }

    public DynamoDbRepository<GameRecord> get() {
        return repository;
    }

    private String getGamesLogTableName() {
        String value = System.getenv(GAMES_LOG_TABLE_ENV);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException("Environment variable " + GAMES_LOG_TABLE_ENV + " is not set or is empty.");
        }
        return value;
    }
}





