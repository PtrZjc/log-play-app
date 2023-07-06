package pl.zajacp.test;

import java.io.IOException;

public class TestData {

    public static final String GAMES_LOG_REQUEST = readResourceFileAsString("exemplary-request.json");
    private static String readResourceFileAsString(String fileName){
        String content;

        try {
            content = new String(TestData.class.getClassLoader().getResourceAsStream(fileName).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }
}
