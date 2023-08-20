package pl.zajacp.test.database;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ TYPE, ANNOTATION_TYPE })
@Retention(RUNTIME)
@ExtendWith(DynamoDbTestExtension.class)
public @interface DynamoDbTest {
    Class<?> entityClass();
    String hashKey();
    String rangeKey();
}