package com.tenghe.corebackend.iam.infrastructure.persistence.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JsonCodec {
    private final ObjectMapper objectMapper;

    public JsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String writeValue(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public <T> T readValue(String json, Class<T> type) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (Exception ex) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> readMap(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception ex) {
            return Collections.emptyMap();
        }
    }
}
