package com.cvent.pangaea;

/**
 * This implementation allows us to resolve values within a configuration at runtime (lazy).  This is useful for
 * non-production environments where we can spin up many regions based on a convention and resolve things like urls
 * and connection pools at runtime.
 *
 * @author bryan
 * @param <T>   The type of each value within the MultiEnvAware map
 */
public class LazyMultiEnvAware<T> extends MultiEnvAware<T> {

    private final TemplateResolver<T> templateResolver;

    public LazyMultiEnvAware(TemplateResolver<T> templateResolver) {
        this.templateResolver = templateResolver;
    }

    @Override
    protected T resolve(String sKey, T value) {
        T seed = templateResolver.resolve(sKey, value);
        addInternalMap(sKey, seed);
        return seed;
    }

}
