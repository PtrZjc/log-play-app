package pl.zajacp.test.infra;

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.annotation.Annotation;

public class ExtensionCommons {

    public static <T extends Annotation> T getAnnotation(ExtensionContext context, Class<T> annotationClass) {
        Class<?> testClass = context.getRequiredTestClass();
        T annotation = testClass.getAnnotation(annotationClass);

        if (annotation == null && testClass.getSuperclass() != null) {
            annotation = testClass.getSuperclass().getAnnotation(annotationClass);
        } else {
            throw new UnsupportedOperationException(String.format("There is no annotation %s on test class %s or its parent",
                    annotationClass.getSimpleName(), testClass.getSimpleName()));
        }

        return annotation;
    }
}
