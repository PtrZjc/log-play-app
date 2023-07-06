package pl.zajacp.test.assertion;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Assertions;
import pl.zajacp.model.PlayerResult;

import java.math.BigDecimal;

@AllArgsConstructor
public class PlayerResultAssertion {
    private final PlayerResult playerResult;
    private final GameRecordAssertion parent;


    public static PlayerResultAssertion assertThat(PlayerResult playerResult) {
        return new PlayerResultAssertion(playerResult, null);
    }

    public PlayerResultAssertion hasName(String playerName) {
        Assertions.assertEquals(playerName, playerResult.getPlayerName());
        return this;
    }

    public PlayerResultAssertion hasScore(BigDecimal playerScore) {
        Assertions.assertEquals(playerScore, playerResult.getPlayerScore());
        return this;
    }

    public PlayerResultAssertion isWinner(boolean isWinner) {
        Assertions.assertEquals(isWinner, playerResult.isWinner());
        return this;
    }

    public PlayerResultAssertion hasComment(String comment) {
        Assertions.assertEquals(comment, playerResult.getComment());
        return this;
    }

    public GameRecordAssertion and() {
        return parent;
    }
}
