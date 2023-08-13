package pl.zajacp.test.domain;

import pl.zajacp.model.GameRecord;
import pl.zajacp.model.PlayerResult;

import java.util.ArrayList;
import java.util.List;

import static pl.zajacp.repository.GameLogRepositoryCommons.GLOBAL_USER;
import static pl.zajacp.test.domain.PlayerResultBuilder.*;
import static pl.zajacp.test.utils.TestData.DURATION;
import static pl.zajacp.test.utils.TestData.GAME_DATE;
import static pl.zajacp.test.utils.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.utils.TestData.GAME_NAME;
import static pl.zajacp.test.utils.TestData.PLAYER_1_NAME;
import static pl.zajacp.test.utils.TestData.PLAYER_1_SCORE;
import static pl.zajacp.test.utils.TestData.PLAYER_2_NAME;
import static pl.zajacp.test.utils.TestData.PLAYER_2_SCORE;
import static pl.zajacp.test.utils.TestData.PLAYER_3_NAME;
import static pl.zajacp.test.utils.TestData.PLAYER_3_SCORE;
import static pl.zajacp.test.utils.TestData.PLAYER_4_NAME;
import static pl.zajacp.test.utils.TestData.PLAYER_4_SCORE;
import static pl.zajacp.test.utils.TestData.PLAYER_5_NAME;
import static pl.zajacp.test.utils.TestData.PLAYER_5_SCORE;
import static pl.zajacp.test.utils.TestData.TIMESTAMP;

public class GameRecordBuilder {
    private Long timestamp = TIMESTAMP;
    private String gameName = GAME_NAME;
    private String gameDate = GAME_DATE;
    private String gameDescription = GAME_DESCRIPTION;
    private boolean solo;
    private String duration = DURATION;
    private final List<PlayerResult> playerResults = new ArrayList<>();

    public static GameRecordBuilder aGameRecord() {
        return new GameRecordBuilder();
    }


    public GameRecord build() {
        return new GameRecord(GLOBAL_USER, timestamp, gameName, gameDate, gameDescription, solo, duration, playerResults);
    }

    public GameRecordBuilder withStandard5PlayersResult() {
        withPlayerResult(
                aPlayerResult()
                        .withPlayerName(PLAYER_1_NAME)
                        .withPlayerScore(PLAYER_1_SCORE)
                        .withIsWinner(true).build())
                .withPlayerResult(aPlayerResult()
                        .withPlayerName(PLAYER_2_NAME)
                        .withPlayerScore(PLAYER_2_SCORE).build())
                .withPlayerResult(aPlayerResult()
                        .withPlayerName(PLAYER_3_NAME)
                        .withPlayerScore(PLAYER_3_SCORE).build())
                .withPlayerResult(aPlayerResult()
                        .withPlayerName(PLAYER_4_NAME)
                        .withPlayerScore(PLAYER_4_SCORE).build())
                .withPlayerResult(aPlayerResult()
                        .withPlayerName(PLAYER_5_NAME)
                        .withPlayerScore(PLAYER_5_SCORE).build());
        return this;
    }

    public GameRecordBuilder withTimestamp(Long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public GameRecordBuilder withGameName(String gameName) {
        this.gameName = gameName;
        return this;
    }

    public GameRecordBuilder withGameDate(String gameDate) {
        this.gameDate = gameDate;
        return this;
    }

    public GameRecordBuilder withGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
        return this;
    }

    public GameRecordBuilder withSolo(boolean solo) {
        this.solo = solo;
        return this;
    }

    public GameRecordBuilder withDuration(String duration) {
        this.duration = duration;
        return this;
    }

    public GameRecordBuilder withPlayerResult(PlayerResult playerResult) {
        playerResults.add(playerResult);
        return this;
    }
}
