
package com.cvent.pangaea.filter;

import com.cvent.pangaea.MultiEnvAware;
import com.cvent.pangaea.util.EnvironmentUtil;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

/**
 * @author n.golani
 *This filter should be registered in dropwizard service run method as ContainerRequestFilter
 * and also ContainerResponseFilter.
 *
 *This filter reads query string of all incoming requests and if environment queryParam exists
 *then sets the environment value in ThreadLocal field so that it can be accessed at any layer.
 *
 *This filter will be invoked before sending out response so that ThreadLocal variable is cleaned.
 *Cleaning of threadLocal variable is important as some frameworks create thread-pools 
 *and same threads will be re-used for different requests and if variable has values from previous
 *requests it will lead to errors.
 */
public class EnvironmentIdentifierFilter implements ContainerRequestFilter, ContainerResponseFilter {

    
    @Override
    public ContainerRequest filter(ContainerRequest request) {
        
        String environmentInRequest = getEnvParamFromRequest(request);
        
        if (environmentInRequest != null) {
            EnvironmentUtil.setEnvironment(environmentInRequest);
        }
        return request;
    }

    private String getEnvParamFromRequest(ContainerRequest request) {
        if (request.getQueryParameters() != null && !request.getQueryParameters().isEmpty()
                && request.getQueryParameters().containsKey(
                        MultiEnvAware.ENVIRONMENT)) {
            return request.getQueryParameters()
                    .get(MultiEnvAware.ENVIRONMENT).get(0);
        }
        
        return null;
    }

    @Override
    public ContainerResponse filter(ContainerRequest request,
            ContainerResponse response) {
        EnvironmentUtil.removeEnvironment();
        return response;
    }

}
