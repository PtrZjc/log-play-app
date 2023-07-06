package pl.zajacp.test.assertion;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import pl.zajacp.model.GameRecord;

@AllArgsConstructor
public class GameRecordAssertion {

    private final GameRecord gameRecord;

    public static GameRecordAssertion assertThat(GameRecord gameRecord) {
        return new GameRecordAssertion(gameRecord);
    }

    public GameRecordAssertion hasTimestamp(Long timestamp) {
        Assertions.assertEquals(timestamp, gameRecord.getTimestamp());
        return this;
    }

    public GameRecordAssertion hasGameName(String gameName) {
        Assertions.assertEquals(gameName, gameRecord.getGameName());
        return this;
    }

    public GameRecordAssertion hasGameDate(String gameDate) {
        Assertions.assertEquals(gameDate, gameRecord.getGameDate());
        return this;
    }

    public GameRecordAssertion hasGameDescription(String gameDescription) {
        Assertions.assertEquals(gameDescription, gameRecord.getGameDescription());
        return this;
    }

    public GameRecordAssertion isSolo(boolean solo) {
        Assertions.assertEquals(solo, gameRecord.isSolo());
        return this;
    }

    public GameRecordAssertion hasDuration(String duration) {
        Assertions.assertEquals(duration, gameRecord.getDuration());
        return this;
    }

    public PlayerResultAssertion withPlayerResultOf(String playerName) {
        var playerResult = gameRecord.getPlayerResults().stream()
                .filter(p -> playerName.equals(p.getPlayerName()))
                .findFirst();
        Assertions.assertTrue(playerResult.isPresent());
        return new PlayerResultAssertion(playerResult.get(), this);
    }
}