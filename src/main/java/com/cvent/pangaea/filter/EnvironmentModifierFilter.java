package com.cvent.pangaea.filter;

import com.cvent.pangaea.MultiEnvAware;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerRequestFilter;
import org.apache.commons.lang.ArrayUtils;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.net.URI;

/**
 * Modifies the environment parameter from the request query string parameters for production environment.
 */
@Provider
public class EnvironmentModifierFilter extends QueryStringModifierFilter {

    /**
     * constructor
     * example usage: EnvironmentModifierFilter("P2", "production", "prod", "pr01")
     * @param toValue the value to replace the environment query parameter with
     * @param fromValues the list of environment query parameter values that need to be replaced
     */
    public EnvironmentModifierFilter(String toValue, String... fromValues) {
        super(MultiEnvAware.ENVIRONMENT, toValue, fromValues);
    }

    /**
     * constructor
     * @param toValue the value to replace the environment query parameter with
     */
    public EnvironmentModifierFilter(String toValue) {
        this(toValue, null);
    }
}

/**
 * Modifies a specific query string parameter in a request
 */
@Provider
class QueryStringModifierFilter implements ContainerRequestFilter {

    private final String queryParamToModify;
    private final String[] fromValues;
    private final String toValue;

    /**
     * constructor.
     * @param queryParamToModify the name of the query string parameter to modify
     * @param toValue the value to replace the environment query parameter with
     * @param fromValues the list of environment query parameter values that need to be replaced
     */
    QueryStringModifierFilter(String queryParamToModify, String toValue, String... fromValues) {
        if (null == toValue) {
            throw new IllegalArgumentException("'toValue' cannot be null");
        }
        if (null == queryParamToModify) {
            throw new IllegalArgumentException("'queryParamToModify' cannot be null");
        }
        this.queryParamToModify = queryParamToModify;
        this.fromValues = fromValues;
        this.toValue = toValue;
    }

    /**
     * constructor
     * @param queryParamToModify the name of the query string parameter to modify
     * @param toValue the value to replace the environment query parameter with
     */
    QueryStringModifierFilter(String queryParamToModify, String toValue) {
        this(queryParamToModify, toValue, null);
    }

    @Override
    public ContainerRequest filter(ContainerRequest request) {
        // only if the query parameters contain the configured name
        // and one of the values from "fromValues"
        if (request.getQueryParameters() != null
                && !request.getQueryParameters().isEmpty()
                && request.getQueryParameters().containsKey(queryParamToModify)
                && ArrayUtils.contains(fromValues,
                  request.getQueryParameters().getFirst(queryParamToModify))) {

            URI modifiedUri = getModifiedUri(request.getRequestUri());

            request.setUris(request.getBaseUri(), modifiedUri);
        }
        return request;
    }

    private URI getModifiedUri(URI originalUri) {
        try {
            return UriBuilder.fromUri(originalUri)
                    .replaceQueryParam(queryParamToModify, toValue)
                    .build();
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }
}
