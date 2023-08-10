package pl.zajacp.rest;

import pl.zajacp.model.GameRecord;
import pl.zajacp.model.GamesLog;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PutRequestValidator {

    public final static String REQUIRED_PARAM = "Required parameter is missing";
    public final static String INVALID_DATE = "Invalid date format. Expected 'yyyy-MM-dd'";
    public final static String NON_EMPTY_REQUIRED = "Non-empty parameter required";

    private final static List<RequiredProperty> GAME_RECORD_REQUIRED_PROPERTIES = List.of(
            new RequiredProperty("gameName", REQUIRED_PARAM,
                    gr -> gr.getGameName() == null || gr.getGameName().isEmpty()),
            new RequiredProperty("timestamp", REQUIRED_PARAM,
                    gr -> gr.getTimestamp() == null),
            new RequiredProperty("gameDate", REQUIRED_PARAM,
                    gr -> gr.getGameDate() == null),
            new RequiredProperty("gameDate", INVALID_DATE,
                    gr -> !isValidDate(gr.getGameDate())),
            new RequiredProperty("playerResults", NON_EMPTY_REQUIRED,
                    gr -> gr.getPlayerResults() == null || gr.getPlayerResults().isEmpty())
    );

    public static Map<String, String> validateGamesLog(GamesLog gamesLog) {
        return gamesLog == null || gamesLog.games() == null || gamesLog.games().isEmpty() ?
                Map.of("gamesLog", "GamesLog cannot be null or empty") :
                IntStream.range(0, gamesLog.games().size())
                        .boxed()
                        .map(i -> new IndexedGameRecordErrors(i, validateGameRecord(gamesLog.games().get(i))))
                        .filter(igre -> !igre.gameRecordErrors().isEmpty())
                        .flatMap(igre -> igre.gameRecordErrors()
                                .entrySet().stream()
                                .map(entry -> IndexedPropertyError.of(igre, entry)))
                        .collect(Collectors.toMap(
                                ipe -> String.format("games[%s].%s", ipe.index(), ipe.propertyName()),
                                IndexedPropertyError::errorMessage));
    }

    private static Map<String, String> validateGameRecord(GameRecord record) {
        var failingProperties = GAME_RECORD_REQUIRED_PROPERTIES.stream()
                .filter(p -> p.failingPredicate.test(record))
                .collect(Collectors.toMap(
                        rp -> rp.propertyName,
                        rp -> rp.errorMessage
                ));
        if (!failingProperties.containsKey("playerResults")) {
            failingProperties.putAll(validatePlayerNames(record));
        }
        return failingProperties;
    }

    private static Map<String, String> validatePlayerNames(GameRecord record) {
        return IntStream.range(0, record.getPlayerResults().size())
                .boxed()
                .filter(i -> record.getPlayerResults().get(i).getPlayerName() == null || record.getPlayerResults().get(i).getPlayerName().isEmpty())
                .collect(Collectors.toMap(i -> "playerResults[" + i + "].playerName", i -> REQUIRED_PARAM));
    }

    private static boolean isValidDate(String date) {
        if (date == null) return false;
        try {
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private record RequiredProperty(
            String propertyName,
            String errorMessage,
            Predicate<GameRecord> failingPredicate
    ) {
    }

    private record IndexedGameRecordErrors(Integer index, Map<String, String> gameRecordErrors) {
    }

    private record IndexedPropertyError(Integer index, String propertyName, String errorMessage) {
        private static IndexedPropertyError of(IndexedGameRecordErrors igre, Map.Entry<String, String> propertyNameToErrorMessage) {
            return new IndexedPropertyError(igre.index(), propertyNameToErrorMessage.getKey(), propertyNameToErrorMessage.getValue());
        }
    }
}
