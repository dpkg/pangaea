
package com.cvent.pangaea.interceptor;

import retrofit.RequestInterceptor;

import com.cvent.pangaea.MultiEnvAware;
import com.cvent.pangaea.util.EnvironmentUtil;

/**
 * This is request interceptor for RetrofitClients so that environment is appended
 * as query param in all the requests. 
 * It takes environment either from ThreadLocal set in {@link EnvironmentIdentifierFilter}
 * or passed in the constructor.
 *
 * @author n.golani
 *
 */
public class EnvironmentInterceptor implements RequestInterceptor {

    private String environment;
    
    public EnvironmentInterceptor() {
    }
    
    public EnvironmentInterceptor(String environment) {
        this.environment = environment;
    }
    
    @Override
    public void intercept(RequestFacade request) {
        if (environment != null && !environment.isEmpty()) {
            request.addQueryParam(MultiEnvAware.ENVIRONMENT, environment);
        } else if (EnvironmentUtil.getEnvironment() != null
                && !EnvironmentUtil.getEnvironment().isEmpty()) {
            request.addQueryParam(MultiEnvAware.ENVIRONMENT, EnvironmentUtil.getEnvironment());
        }
        
    }

}
