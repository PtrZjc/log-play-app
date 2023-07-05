package pl.zajacp;

import java.io.IOException;

public class TestUtils {

    static final String EXEMPLARY_REQUEST = readResourceFileAsString("exemplary-request.json");
    private static String readResourceFileAsString(String fileName){
        String content;
        try {
            content = new String(TestUtils.class.getClassLoader().getResourceAsStream(fileName).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return content;
    }
}
