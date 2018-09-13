package com.cvent.pangaea;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This is just an example configuration object that is meant to be used as part of the dropwizard configuration
 * hierarchy.
 *
 * @author bryan
 */
class ExampleConfiguration {

    /**
     * This property uses a custom deserializer so that we can inject the template resolver after this gets deserialized
     * from a configuration.
     */
    @JsonProperty
    private MultiEnvAware<MultiEnvConfig> environmentConfig;

    @JsonIgnore
    public MultiEnvAware<String> getSurveyUrlDefaultDomains() {
        return environmentConfig.convert((env, conf) -> conf.getSurveyUrlDefaultDomain(),
                new SiloTemplateResolver<>(String.class, new ObjectMapper()));
    }

    @JsonIgnore
    public MultiEnvAware<String> getSurveyUrlAppRoots() {
        return environmentConfig.convert((env, conf) -> conf.getSurveyUrlAppRoot(),
                new SiloTemplateResolver<>(String.class, new ObjectMapper()));
    }

    @JsonIgnore
    public MultiEnvAware<String> getSupportedEnvironments() {
        return environmentConfig.convert((env, conf) -> env);
    }

    public void setEnvironmentConfig(MultiEnvAware<MultiEnvConfig> environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    public MultiEnvAware<MultiEnvConfig> getEnvironmentConfig() {
        return environmentConfig.convert((env, conf) -> conf, new SiloTemplateResolver<>(MultiEnvConfig.class, 
            new ObjectMapper()));
    }

    /**
     * Encapsulate properties that are environment specific
     */
    static class MultiEnvConfig extends BaseEnvironmentConfiguration {

        private String surveyUrlAppRoot;
        private String surveyUrlDefaultDomain;

        public String getSurveyUrlAppRoot() {
            return surveyUrlAppRoot;
        }

        public void setSurveyUrlAppRoot(String surveyUrlAppRoot) {
            this.surveyUrlAppRoot = surveyUrlAppRoot;
        }

        public String getSurveyUrlDefaultDomain() {
            return surveyUrlDefaultDomain;
        }

        public void setSurveyUrlDefaultDomain(String surveyUrlDefaultDomain) {
            this.surveyUrlDefaultDomain = surveyUrlDefaultDomain;
        }

    }

}
