package com.tenghe.corebackend.device.infrastructure.persistence.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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

  @SuppressWarnings("unchecked")
  public List<String> readStringList(String json) {
    if (json == null || json.isBlank()) {
      return Collections.emptyList();
    }
    try {
      return objectMapper.readValue(json, List.class);
    } catch (Exception ex) {
      return Collections.emptyList();
    }
  }
}
