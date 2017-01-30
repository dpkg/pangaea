package com.cvent.pangaea;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

//
//default_multi_env_config: &default_env_config
//    database: &default_db
//        checkConnectionWhileIdle: true
//        driverClass: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
//        initialSize: 5
//        maxConnectionAge: "10s"
//        minSize: 5
//        maxSize: 5
//        validationQuery: "SELECT 1"
//        isLazy: true
//    surveyUrlAppRoot: "/Surveys"
//
//environmentConfig:
//    S115:
//        <<: *default_env_config
//        database:
//            <<: *default_db
//            user: "cvent"
//            password: "n0rth"
//            url: "jdbc:sqlserver://a1-dba-115.a1.cvent.com\\dev_silo:50000;database=CVENT_PROD"
//        reportDatabase:
//            <<: *default_db
//            user: "cvent"
//            password: "n0rth"
//            url: "jdbc:sqlserver://a1-dba-115.a1.cvent.com\\dev_silo:50000;database=CVENT_REPORT"
//        surveyUrlDefaultDomain: "silo115-guest.a1.cvent.com"
//        customFieldServiceDomain: "http://staging-wiz-01.cvent.net:8122/customfields"
//        defaultEnvironmentConfiguration: true
//    S116:
//        <<: *default_env_config
//        database:
//            <<: *default_db
//            user: "cvent"
//            password: "n0rth"
//            url: "jdbc:sqlserver://a1-dba-116.a1.cvent.com\\dev_silo:50000;database=CVENT_PROD"
//        reportDatabase:
//            <<: *default_db
//            user: "cvent"
//            password: "n0rth"
//            url: "jdbc:sqlserver://a1-dba-116.a1.cvent.com\\dev_silo:50000;database=CVENT_REPORT"
//        surveyUrlDefaultDomain: "silo116-guest.a1.cvent.com"
//        customFieldServiceDomain: "http://staging-wiz-01.cvent.net:8122/customfields"
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
                new SiloTemplateResolver<>(String.class));
    }

    @JsonIgnore
    public MultiEnvAware<String> getSurveyUrlAppRoots() {
        return environmentConfig.convert((env, conf) -> conf.getSurveyUrlAppRoot(),
                new SiloTemplateResolver<>(String.class));
    }

    @JsonIgnore
    public MultiEnvAware<String> getSupportedEnvironments() {
        return environmentConfig.convert((env, conf) -> env);
    }

    public void setEnvironmentConfig(MultiEnvAware<MultiEnvConfig> environmentConfig) {
        this.environmentConfig = environmentConfig;
    }

    public MultiEnvAware<MultiEnvConfig> getEnvironmentConfig() {
        return environmentConfig.convert((env, conf) -> conf, new SiloTemplateResolver<>(MultiEnvConfig.class));
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
