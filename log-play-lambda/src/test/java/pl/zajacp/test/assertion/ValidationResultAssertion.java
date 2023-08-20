package pl.zajacp.test.assertion;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import pl.zajacp.shared.ObjMapper;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ValidationResultAssertion {

    private final Map<String, String> validationErrors;
    private String lastPropertyChecked;

    public static ValidationResultAssertion assertThat(String responseBodyWithErrors) {
        Map<String, String> errors = null;
        try {
            var bodyMap = ObjMapper.INSTANCE.get().readValue(responseBodyWithErrors,
                    new TypeReference<HashMap<String, HashMap<String, String>>>() {
                    });
            errors = bodyMap.get("validationErrors");
        } catch (JsonProcessingException e) {
            Assertions.fail("Provided responseBodyString is not a valid response body with errors");
        }
        return assertThat(errors);
    }

    public static ValidationResultAssertion assertThat(Map<String, String> validationErrors) {
        Assertions.assertNotNull(validationErrors, "No validation errors found");
        return new ValidationResultAssertion(validationErrors);
    }

    public ValidationResultAssertion hasErrorQuantityOf(int errorQuantity) {
        Assertions.assertEquals(errorQuantity , validationErrors.size(),
                () -> String.format("Expected %s errors, but was %s instead: %s", errorQuantity, validationErrors.size(), validationErrors));
        return this;
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