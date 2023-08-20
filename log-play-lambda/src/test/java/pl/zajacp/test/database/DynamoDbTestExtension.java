package pl.zajacp.test.database;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import pl.zajacp.repository.DynamoDbRepository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;
import java.util.List;

import static pl.zajacp.test.database.DbTableHelper.createTableWithCompositePrimaryKey;
import static pl.zajacp.test.database.DbTableHelper.deleteTable;
import static pl.zajacp.test.infra.ExtensionCommons.getAnnotation;

public class DynamoDbTestExtension implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private static final boolean USE_LOCAL_DB = false;

    private static String TABLE_NAME;
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(DynamoDbTestExtension.class);
    private static final List<Class<?>> DEPENDENCIES = List.of(DynamoDbRepository.class);

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return DEPENDENCIES.contains(parameterContext.getParameter().getType());
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> paramType = parameterContext.getParameter().getType();
        ExtensionContext.Store store = extensionContext.getStore(NAMESPACE);

        if (paramType == DynamoDbClient.class) return store.get(DynamoDbClient.class);
        if (paramType == DynamoDbRepository.class) return store.get(DynamoDbRepository.class);
        throw new UnsupportedOperationException("Undefined parameter: " + paramType);
    }

    @Override
    public void beforeAll(ExtensionContext context) {
        TABLE_NAME = getAnnotation(context, DynamoDbTest.class).entityClass().getSimpleName() + "Table";

        if (!USE_LOCAL_DB) DynamoDbContainer.startContainer();

        String path = USE_LOCAL_DB ? "http://localhost:8000" : DynamoDbContainer.getLocalhostPath();

        DynamoDbClient client = DynamoDbClient.builder()
                .endpointOverride(URI.create(path))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local")))
                .region(Region.EU_CENTRAL_1).build();

        var repository = new DynamoDbRepository<>(client, TABLE_NAME, getAnnotation(context, DynamoDbTest.class).entityClass());

        context.getStore(NAMESPACE).put(DynamoDbClient.class, client);
        context.getStore(NAMESPACE).put(DynamoDbRepository.class, repository);
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        createTableWithCompositePrimaryKey(
                getDependencyFromContext(context, DynamoDbClient.class),
                getAnnotation(context, DynamoDbTest.class).hashKey(),
                getAnnotation(context, DynamoDbTest.class).rangeKey(),
                TABLE_NAME);
    }

    @Override
    public void afterEach(ExtensionContext context) {
        deleteTable(getDependencyFromContext(context, DynamoDbClient.class), TABLE_NAME);
    }

    private static <T> T getDependencyFromContext(ExtensionContext context, Class<T> dependencyClass) {
        return context.getStore(NAMESPACE).get(dependencyClass, dependencyClass);
    }
}
