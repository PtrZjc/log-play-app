package pl.zajacp.repository;

public class GameLogRepositoryCommons {

    public static final String GAME_NAME_HASH_KEY = "gameName";
    public static final String TIMESTAMP_RANGE_KEY = "timestamp";

    public static ItemQueryKey getGameRecordKey(String gameName, Long timestamp) {
        return ItemQueryKey.of(
                GAME_NAME_HASH_KEY, gameName,
                TIMESTAMP_RANGE_KEY, timestamp);
    }
}
