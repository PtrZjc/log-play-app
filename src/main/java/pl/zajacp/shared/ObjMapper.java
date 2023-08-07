package pl.zajacp.shared;

import com.fasterxml.jackson.databind.ObjectMapper;

public enum ObjMapper {
    INSTANCE;

    private final ObjectMapper mapper;

    ObjMapper() {
        mapper = new ObjectMapper();
    }

    public ObjectMapper get() {
        return mapper;
    }
}