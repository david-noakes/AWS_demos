package au.gov.qld.idm.neo.testing.utils;

import com.amazonaws.util.StringUtils;
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Project notifications-v2_application
 * Created on 14 Nov 2017.
 */
public enum JsonUtils {

    INSTANCE;

    private final ObjectMapper objectMapper;
    private final ObjectWriter writer;

    JsonUtils() {
        objectMapper = Jackson.
                getObjectMapper().
                configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false).
                setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH));

        objectMapper.registerModule(new JavaTimeModule());

        writer = objectMapper.writer();
    }

    public String toJsonString(Object value) {
        try {
            return writer.writeValueAsString(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public <T> T fromJsonString(String json, Class<T> clazz) {
        if (json == null) {
            return null;
        }

        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse Json String.", e);
        }
    }

    public <T> List<T> fromJsonStringAsList(String json, Class<T> clazz) {
        if (StringUtils.isNullOrEmpty(json)) {
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to parse Json String.", e);
        }
    }
}