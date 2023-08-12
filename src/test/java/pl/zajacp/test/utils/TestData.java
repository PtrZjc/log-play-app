package pl.zajacp.test.utils;

import lombok.SneakyThrows;

import java.math.BigDecimal;

public class TestData {

    public final static Long TIMESTAMP = 1688666155L;
    public final static Long TIMESTAMP_2 = 1689020155L;
    public final static String GAME_NAME = "Caverna";
    public final static String GAME_NAME_2 = "Agricola";
    public final static String GAME_DATE = "2023-07-06";
    public final static String INVALID_GAME_DATE = "15/05/2023";
    public final static String GAME_DESCRIPTION = "description";
    public final static String DIFFERENT_DESCRIPTION = "some other description";
    public final static String DURATION = "3:00";
    public final static String COMMENT = "comment";
    public final static String PLAYER_1_NAME = "Piotr";
    public final static String PLAYER_2_NAME = "Ilona";
    public final static String PLAYER_3_NAME = "Maciej";
    public final static String PLAYER_4_NAME = "Kamil";
    public final static String PLAYER_5_NAME = "Grzesiu";
    public final static BigDecimal PLAYER_1_SCORE = BigDecimal.valueOf(100L);
    public final static BigDecimal PLAYER_2_SCORE = BigDecimal.valueOf(110L);
    public final static BigDecimal PLAYER_3_SCORE = BigDecimal.valueOf(90L);
    public final static BigDecimal PLAYER_4_SCORE = BigDecimal.valueOf(10L);
    public final static BigDecimal PLAYER_5_SCORE = BigDecimal.valueOf(70L);

    @SneakyThrows
    public static String getExemplaryGamesLogRequest() {
        return new String(TestData.class.getClassLoader().getResourceAsStream("exemplary-request.json").readAllBytes());
    }
}
