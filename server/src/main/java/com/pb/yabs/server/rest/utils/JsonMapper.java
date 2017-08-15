package com.pb.yabs.server.rest.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * @author Pavel Borsky
 * date 2017-08-15
 */
public class JsonMapper {
    private static final Logger logger = LoggerFactory.getLogger(JsonMapper.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonMapper() {
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public Optional<String> toJson(Object object) {
        try {
            return Optional.of(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize data", e);
            return Optional.empty();
        }
    }
}
