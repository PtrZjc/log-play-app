package pl.zajacp.shared;

public class ObjectMapper {

    private static class InstanceHolder {
        private static final com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
    }

    public static com.fasterxml.jackson.databind.ObjectMapper get() {
        return InstanceHolder.mapper;
    }
}
