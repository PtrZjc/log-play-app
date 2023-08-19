package pl.zajacp.test.builder;

import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static pl.zajacp.test.builder.GameRecordBuilder.aGameRecord;

public class GamesLogBuilder {
    private List<GameRecord> gamesLog = new ArrayList<>();

    public static GamesLogBuilder aGamesLog() {
        return new GamesLogBuilder();
    }

    public GamesLog build() {
        return new GamesLog(gamesLog);
    }

    public GamesLogBuilder withGameRecord(GameRecord gameRecord) {
        gamesLog.add(gameRecord);
        return this;
    }

    public GamesLogBuilder withMultipleDefaultGameRecordsStartingWithTimestamp(Long timestamp, Integer quantity) {
        IntStream.range(0, quantity)
                .mapToObj(i -> aGameRecord()
                        .withStandard5PlayersResult()
                        .withTimestamp(timestamp + i).build())
                .forEach(gr -> gamesLog.add(gr));
        return this;
    }
}