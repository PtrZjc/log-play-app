package pl.zajacp.test.utils;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class DynamoDbContainer {
    private static GenericContainer<?> CONTAINER;

    public static GenericContainer<?> getContainer() {
        if (CONTAINER == null) {
            var containerName = DockerImageName.parse("amazon/dynamodb-local:latest");
            CONTAINER = new GenericContainer<>(containerName)
                    .withExposedPorts(8000)
                    .withReuse(true)
                    .withNetwork(null);
            CONTAINER.start();
        }
        return CONTAINER;
    }
    public static void startContainer() {
        getContainer();
    }

    @NotNull
    public static String getLocalhostPath() {
        return "http://localhost:" + getContainer().getFirstMappedPort();
    }
}
