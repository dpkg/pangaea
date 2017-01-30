
package com.cvent.pangaea.util;

/**
 * @author n.golani
 *This is the utility class which contains threadLocal variable environment which is set 
 *in EnvironmentIdentifierFilter.
 *
 *Each thread holds an implicit reference to its copy of a thread-local
 * variable as long as the thread is alive.
 * 
 * In case threadPools are created then ThreadLocal variable should be cleaned explicitly
 * so that wrong values are not passes from one request to another. The cleaning of threadLocal 
 * variable is done in EnvironmentIdentifierFilter.
 * 
 * This utility should not be used if asynchronous execution is performed as the async flow
 * will be performed in different thread which cannot have access to threadLocal variable of 
 * parent thread or request initiating thread. 
 *
 */
public final class EnvironmentUtil {
    
    private static final ThreadLocal<String> ENVIRONMENT = new ThreadLocal<>();

    /**
     * Private constructor for Utility class
     */
    private EnvironmentUtil() {
        
    }
    /**
     * @return the environment
     */
    public static String getEnvironment() {
        return ENVIRONMENT.get();
    }
    
    /**
     * Set the environment for this thread local instance.
     * 
     * @param environment 
     */
    public static void setEnvironment(String environment) {
        EnvironmentUtil.ENVIRONMENT.set(environment);
    }
    
    /**
     * Remove the thread local instance
     */
    public static void removeEnvironment() {
        ENVIRONMENT.remove();
    }

}
