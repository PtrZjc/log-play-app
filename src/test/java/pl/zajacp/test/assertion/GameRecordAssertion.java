package pl.zajacp.test.assertion;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import pl.zajacp.model.GameRecord;

import static pl.zajacp.test.TestData.PLAYER_1_NAME;
import static pl.zajacp.test.TestData.PLAYER_1_SCORE;
import static pl.zajacp.test.TestData.PLAYER_2_NAME;
import static pl.zajacp.test.TestData.PLAYER_2_SCORE;
import static pl.zajacp.test.TestData.PLAYER_3_NAME;
import static pl.zajacp.test.TestData.PLAYER_3_SCORE;
import static pl.zajacp.test.TestData.PLAYER_4_NAME;
import static pl.zajacp.test.TestData.PLAYER_4_SCORE;
import static pl.zajacp.test.TestData.PLAYER_5_NAME;
import static pl.zajacp.test.TestData.PLAYER_5_SCORE;

@AllArgsConstructor
public class GameRecordAssertion {

    private final GameRecord gameRecord;

    public static GameRecordAssertion assertThat(GameRecord gameRecord) {
        Assertions.assertNotNull(gameRecord);
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

    public void hasExpected5StandardPlayersResult() {
        withPlayerResultOf(PLAYER_1_NAME)
                .hasScore(PLAYER_1_SCORE)
                .isWinner(true)
                .and()
                .withPlayerResultOf(PLAYER_2_NAME)
                .hasScore(PLAYER_2_SCORE)
                .isWinner(false)
                .and()
                .withPlayerResultOf(PLAYER_3_NAME)
                .hasScore(PLAYER_3_SCORE)
                .isWinner(false)
                .and()
                .withPlayerResultOf(PLAYER_4_NAME)
                .hasScore(PLAYER_4_SCORE)
                .isWinner(false)
                .and()
                .withPlayerResultOf(PLAYER_5_NAME)
                .hasScore(PLAYER_5_SCORE)
                .isWinner(false);
    }
}