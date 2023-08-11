package pl.zajacp;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.zajacp.model.GamesLog;

import java.util.List;

import static pl.zajacp.rest.GameValidator.INVALID_DATE;
import static pl.zajacp.rest.GameValidator.NON_EMPTY_REQUIRED;
import static pl.zajacp.rest.GameValidator.REQUIRED_PARAM;
import static pl.zajacp.rest.GameValidator.validateGameRecord;
import static pl.zajacp.rest.GameValidator.validateGamesLog;
import static pl.zajacp.test.domain.GameRecordBuilder.aGameRecord;
import static pl.zajacp.test.domain.GamesLogBuilder.aGamesLog;
import static pl.zajacp.test.domain.PlayerResultBuilder.aPlayerResult;
import static pl.zajacp.test.domain.ValidationResultAssertion.assertThat;
import static pl.zajacp.test.utils.TestData.INVALID_GAME_DATE;

public class GameValidatorTest {

    @Nested
    class GameRecordValidationTest {

        @Test
        public void shouldFailOnMissingGameName() {
            //given
            var gameRecord = aGameRecord()
                    .withGameName(null).build();
            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("gameName").withDetails(NON_EMPTY_REQUIRED);
        }

        @Test
        public void shouldFailOnEmptyGameName() {
            //given
            var gameRecord = aGameRecord()
                    .withGameName("").build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("gameName").withDetails(NON_EMPTY_REQUIRED);
        }

        @Test
        public void shouldFailOnMissingTimestamp() {
            //given
            var gameRecord = aGameRecord()
                    .withTimestamp(null).build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("timestamp").withDetails(REQUIRED_PARAM);
        }

        @Test
        public void shouldFailOnMissingGameDate() {
            //given
            var gameRecord = aGameRecord()
                    .withGameDate(null).build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("gameDate").withDetails(REQUIRED_PARAM);
        }

        @Test
        public void shouldFailOnInvalidGameDateFormat() {
            //given
            var gameRecord = aGameRecord()
                    .withGameDate("invalid-date").build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("gameDate").withDetails(INVALID_DATE);
        }

        @Test
        public void shouldPassWhenAllPlayerNamesPresent() {
            //given
            var gameRecord = aGameRecord()
                    .withStandard5PlayersResult().build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult).hasNoErrors();
        }

        @Test
        public void shouldFailOnMissingPlayerResults() {
            //given
            var gameRecord = aGameRecord().build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("playerResults").withDetails(NON_EMPTY_REQUIRED);
        }

        @Test
        public void shouldFailOnEachMissingPlayerName() {
            //given
            var gameRecord = aGameRecord()
                    .withPlayerResult(aPlayerResult().withPlayerName(null).build())
                    .withPlayerResult(aPlayerResult().withPlayerName("").build())
                    .build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("playerResults[0].playerName").withDetails(NON_EMPTY_REQUIRED)
                    .hasErrorOnProperty("playerResults[1].playerName").withDetails(NON_EMPTY_REQUIRED);
        }

        @Test
        public void shouldFailOnInvalidDate() {
            //given
            var gameRecord = aGameRecord()
                    .withGameDate(INVALID_GAME_DATE).build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("gameDate").withDetails(INVALID_DATE);
        }

        @Test
        public void shouldReturnFalseForNullDate() {
            //given
            var gameRecord = aGameRecord()
                    .withGameDate(null).build();

            //when
            var validationResult = validateGameRecord(gameRecord);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("gameDate").withDetails(REQUIRED_PARAM);
        }
    }


    @Nested
    class GamesLogValidationTest {

        @Test
        public void shouldFailOnNullGamesLog() {
            //given
            var gamesLog = new GamesLog(null);

            //when
            var validationResult = validateGamesLog(gamesLog);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("games").withDetails(NON_EMPTY_REQUIRED);
        }

        @Test
        public void shouldFailOnEmptyGamesList() {
            //given
            var gamesLog = new GamesLog(List.of());
            //when
            var validationResult = validateGamesLog(gamesLog);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("games").withDetails(NON_EMPTY_REQUIRED);
        }

        @Test
        public void shouldPropagateNestedErrorsWithIndexFormat() {
            //given
            var gamesLog =  aGamesLog()
                    .withGameRecord(aGameRecord().withGameName(null).build())
                    .withGameRecord(aGameRecord().withTimestamp(null).build())
                    .withGameRecord(aGameRecord().withGameDate(INVALID_GAME_DATE).build())
                    .withGameRecord(aGameRecord()
                            .withPlayerResult(aPlayerResult().withPlayerName(null).build())
                            .withPlayerResult(aPlayerResult().withPlayerName(null).build())
                            .withPlayerResult(aPlayerResult().withPlayerName(null).build()).build())
                    .build();

            //when
            var validationResult = validateGamesLog(gamesLog);

            //then
            assertThat(validationResult)
                    .hasErrorOnProperty("games[0].gameName").withDetails(NON_EMPTY_REQUIRED)
                    .hasErrorOnProperty("games[1].timestamp").withDetails(REQUIRED_PARAM)
                    .hasErrorOnProperty("games[2].gameDate").withDetails(INVALID_DATE)
                    .hasErrorOnProperty("games[3].playerResults[0].playerName").withDetails(NON_EMPTY_REQUIRED)
                    .hasErrorOnProperty("games[3].playerResults[1].playerName").withDetails(NON_EMPTY_REQUIRED)
                    .hasErrorOnProperty("games[3].playerResults[2].playerName").withDetails(NON_EMPTY_REQUIRED);
        }
    }
}
