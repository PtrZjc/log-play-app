package pl.zajacp.repository;

public class GameLogRepositoryCommons {

    public static final String USER_HASH_KEY = "userName";
    public static final String TIMESTAMP_RANGE_KEY = "timestamp";
    public static final String GLOBAL_USER = "global";

    public static ItemQueryKey getGameRecordKey(Long timestamp) {
        return getGameRecordKey(timestamp, null);
    }

    public static ItemQueryKey getGameRecordKey(Long timestamp, String user) {
        return ItemQueryKey.of(
                USER_HASH_KEY, user,
                TIMESTAMP_RANGE_KEY, timestamp);
    }

    public static ItemQueryKey getGamesLogKey(String user) {
        return ItemQueryKey.of(USER_HASH_KEY, user);
    }
}
