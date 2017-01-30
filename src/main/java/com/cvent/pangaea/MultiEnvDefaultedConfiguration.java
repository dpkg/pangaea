package com.cvent.pangaea;

/**
 * Support multi environment configuration with default instance
 */
public interface MultiEnvDefaultedConfiguration {

    /**
     * @return {@code true} in case this environment is default.
     * Only one default environment is allowed per instance.
     */
    boolean isDefault();
}
