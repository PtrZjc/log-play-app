package pl.zajacp.repository;

import pl.zajacp.model.GameRecord;

public class GamesLogRepository {

    private final static String GAMES_LOG_TABLE = System.getenv("GAMES_LOG_TABLE");

    private static class InstanceHolder {
        private static final DynamoDbRepository<GameRecord> repository =
                new DynamoDbRepository<>(GAMES_LOG_TABLE, GameRecord .class);

    }

    public static DynamoDbRepository<GameRecord> getInstance() {
        return InstanceHolder.repository;
    }
}
