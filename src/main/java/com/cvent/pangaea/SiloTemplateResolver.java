package com.cvent.pangaea;

import com.fasterxml.jackson.annotation.JsonInclude;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import io.dropwizard.jackson.AnnotationSensitivePropertyNamingStrategy;
import io.dropwizard.jackson.DiscoverableSubtypeResolver;
import io.dropwizard.jackson.FuzzyEnumModule;
import io.dropwizard.jackson.GuavaExtrasModule;
import io.dropwizard.jackson.LogbackModule;
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

    public SiloTemplateResolver(Class<T> classType) {
        ObjectMapper m = new ObjectMapper();
        m.registerModule(new GuavaModule());
        m.registerModule(new LogbackModule());
        m.registerModule(new GuavaExtrasModule());
        m.registerModule(new JodaModule());
        m.registerModule(new JSR310Module());
        m.registerModule(new AfterburnerModule());
        m.registerModule(new FuzzyEnumModule());
        m.setPropertyNamingStrategy(new AnnotationSensitivePropertyNamingStrategy());
        m.setSubtypeResolver(new DiscoverableSubtypeResolver());

        //Setup object mapper to ignore the null properties when serializing the objects
        m.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //Lets be nice and allow additional properties by default.  Allows for more flexible forward/backward 
        //compatibility and works well with jackson addtional properties feature for serialization
        m.configure(FAIL_ON_UNKNOWN_PROPERTIES, false);

        this.classType = classType;
        this.mapper = m;
    }

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
