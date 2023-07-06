package pl.zajacp.test.builder;

import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;

import java.util.ArrayList;
import java.util.List;

public class GamesLogBuilder {
    private List<GameRecord> gamesLog = new ArrayList<>();

    public static GamesLogBuilder aGamesLog() {
        return new GamesLogBuilder();
    }

    public GamesLog build(){
        return new GamesLog(gamesLog);
    }

    public GamesLogBuilder withGameRecord(GameRecord gameRecord) {
        gamesLog.add(gameRecord);
        return this;
    }
}