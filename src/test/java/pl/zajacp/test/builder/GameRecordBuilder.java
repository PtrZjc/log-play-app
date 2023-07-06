package pl.zajacp.test.builder;

import pl.zajacp.model.GameRecord;
import pl.zajacp.model.PlayerResult;

import java.util.ArrayList;
import java.util.List;

import static pl.zajacp.test.TestData.DURATION;
import static pl.zajacp.test.TestData.GAME_DATE;
import static pl.zajacp.test.TestData.GAME_DESCRIPTION;
import static pl.zajacp.test.TestData.GAME_NAME;
import static pl.zajacp.test.TestData.TIMESTAMP;

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
        return new GameRecord(timestamp, gameName, gameDate, gameDescription, solo, duration, playerResults);
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
