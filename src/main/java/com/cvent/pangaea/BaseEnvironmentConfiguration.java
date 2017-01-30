package com.cvent.pangaea;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * This is a base configuration that provides some boilerplate properties used in our multi-env configurations.
 *
 * @author bryan
 */
public class BaseEnvironmentConfiguration implements MultiEnvDefaultedConfiguration, MultiEnvTemplateConfiguration {

    private boolean defaultEnvironmentConfiguration = false;
    private boolean template = false;

    @JsonIgnore
    @Override
    public boolean isDefault() {
        return defaultEnvironmentConfiguration;
    }

    public boolean isDefaultEnvironmentConfiguration() {
        return defaultEnvironmentConfiguration;
    }

    @Override
    public boolean isTemplate() {
        return template;
    }

    public void setDefaultEnvironmentConfiguration(boolean defaultEnvironmentConfiguration) {
        this.defaultEnvironmentConfiguration = defaultEnvironmentConfiguration;
    }

    public void setTemplate(boolean template) {
        this.template = template;
    }

}
