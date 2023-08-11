package pl.zajacp.test.domain;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

@RequiredArgsConstructor
public class ValidationResultAssertion {

    private final Map<String, String> validationErrors;
    private String lastPropertyChecked;

    public static ValidationResultAssertion assertThat(Map<String, String> validationErrors) {
        Assertions.assertNotNull(validationErrors);
        return new ValidationResultAssertion(validationErrors);
    }

    public ValidationResultAssertion hasErrorOnProperty(String property) {
        Assertions.assertTrue(validationErrors.containsKey(property),
                () -> String.format("Validation result does not contain error of expected property \"%s\"", property));
        lastPropertyChecked = property;
        return this;
    }

    public ValidationResultAssertion withDetails(String errorDetails) {
        var actualDetails = validationErrors.get(lastPropertyChecked);
        Assertions.assertEquals(errorDetails, actualDetails,
                () -> String.format("Unexpected error details on property %s", lastPropertyChecked));
        return this;
    }

    public void hasNoErrors() {
        Assertions.assertTrue(validationErrors.isEmpty());
    }
}