package pl.zajacp.shared;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ObjectMapperSingleton {

    private static class InstanceHolder {
        private static final ObjectMapper mapper = new ObjectMapper();
    }

    public static ObjectMapper getInstance() {
        return InstanceHolder.mapper;
    }
}
