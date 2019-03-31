package com.cvent.pangaea.filter;

import com.cvent.pangaea.MultiEnvAware;
import org.apache.commons.lang3.ArrayUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.Provider;
import java.net.URI;

/**
 * Modifies the environment parameter from the request query string parameters for production environment.
 */
@Provider
@PreMatching
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
    public void filter(ContainerRequestContext requestContext) {
        // only if the query parameters contain the configured name
        // and one of the values from "fromValues"
        if (requestContext.getUriInfo().getQueryParameters() != null
                && !requestContext.getUriInfo().getQueryParameters().isEmpty()
                && requestContext.getUriInfo().getQueryParameters().containsKey(queryParamToModify)
                && ArrayUtils.contains(fromValues,
                  requestContext.getUriInfo().getQueryParameters().getFirst(queryParamToModify))) {

            URI modifiedUri = getModifiedUri(requestContext.getUriInfo().getRequestUri());

            requestContext.setRequestUri(requestContext.getUriInfo().getBaseUri(), modifiedUri);
        }
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
