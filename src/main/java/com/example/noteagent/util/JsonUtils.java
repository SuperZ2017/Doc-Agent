package com.example.noteagent.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

    private JsonUtils() {
    }

    public static String extractJsonObject(String raw) {
        if (raw == null) {
            return "";
        }
        String text = raw.trim();
        if (text.startsWith("```")) {
            text = text.replaceFirst("^```(?:json)?\\s*", "");
            text = text.replaceFirst("\\s*```$", "");
        }
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end >= start) {
            return text.substring(start, end + 1);
        }
        return text;
    }

    public static <T> T readJson(ObjectMapper objectMapper, String raw, Class<T> type) throws JsonProcessingException {
        return objectMapper.readValue(extractJsonObject(raw), type);
    }

    public static String toPrettyJson(ObjectMapper objectMapper, Object value) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalStateException("Failed to serialize JSON", ex);
        }
    }
}
