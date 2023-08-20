package pl.zajacp.test.builder;

import pl.zajacp.model.PlayerResult;

import java.math.BigDecimal;

import static pl.zajacp.test.TestData.COMMENT;
import static pl.zajacp.test.TestData.PLAYER_1_NAME;
import static pl.zajacp.test.TestData.PLAYER_1_SCORE;

public class PlayerResultBuilder {
    private String playerName = PLAYER_1_NAME;
    private BigDecimal playerScore = PLAYER_1_SCORE;
    private boolean isWinner;
    private String comment = COMMENT;

    public static PlayerResultBuilder aPlayerResult() {
        return new PlayerResultBuilder();
    }
    public PlayerResult build() {
        return new PlayerResult(playerName, playerScore, isWinner, comment);
    }

    public PlayerResultBuilder withPlayerName(String playerName) {
        this.playerName = playerName;
        return this;
    }

    public PlayerResultBuilder withPlayerScore(BigDecimal playerScore) {
        this.playerScore = playerScore;
        return this;
    }

    public PlayerResultBuilder withIsWinner(boolean isWinner) {
        this.isWinner = isWinner;
        return this;
    }

    public PlayerResultBuilder withComment(String comment) {
        this.comment = comment;
        return this;
    }
}