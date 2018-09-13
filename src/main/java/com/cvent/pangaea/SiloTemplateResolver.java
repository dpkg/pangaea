package com.cvent.pangaea;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

/**
 * An implementation that assumes we use S<silo number> format for environment names.
 *
 * For example, XXX could be replaced dynamically based on some convention
 * environmentConfig:
 *     template:
 *         <<: *default_env_config
 *         database:
 *             <<: *default_db
 *             url: "jdbc:log4jdbc:sqlserver://a1-dba-XXX.a1.cvent.com\\dev_silo;database=CVENT_PROD"
 *         template: true
 * @param <T> The type
 * T that will get dynamically resolved at runtime
 */
public class SiloTemplateResolver<T> implements TemplateResolver<T> {

    private static final String SILO_REPLACEMENT_DIGITS = "XXX";
    private final Class<T> classType;
    private final ObjectMapper mapper;

    public SiloTemplateResolver(Class<T> classType, ObjectMapper mapper) {
        this.classType = classType;
        this.mapper = mapper;
    }

    @Override
    public T resolve(String key, T value) {
        try {
            String json = mapper.writeValueAsString(value);
            json = json.replace(SILO_REPLACEMENT_DIGITS, key.substring(1));
            return mapper.readValue(json, classType);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
