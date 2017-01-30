package com.cvent.pangaea;

/**
 * Thrown by {@link MultiEnvAware} class in case environment was <code>null</code>,
 * empty or no configuration was mapped for requested environment
 */
@SuppressWarnings("serial")
public class MultiEnvSupportException extends RuntimeException {

    public MultiEnvSupportException(String msg) {
        super(msg);
    }

}
