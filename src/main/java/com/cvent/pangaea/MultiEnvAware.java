package com.cvent.pangaea;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Encapsulate multi environment aware logic
 *
 * @param <T> - configuration object for specific environment
 */
public class MultiEnvAware<T> implements Map<String, T> {

    private static final Logger LOG = LoggerFactory.getLogger(MultiEnvAware.class);

    private final Map<String, T> map = new ConcurrentHashMap<>();
    private String defaultEnvironment = null;
    private static final String DEFAULT_TEMPLATE_ENVIRONMENT_NAME = "NONE";    
    private String templateEnvironment = DEFAULT_TEMPLATE_ENVIRONMENT_NAME;

    /**
     * A function that accepts an environment and creates the multi-env value for that environment
     */
    private Function<String, T> creationFunction;

    /**
     * A constant that allows us to reference the same query string param across all our services
     */
    public static final String ENVIRONMENT = "environment";

    /**
     * Utility method for MultiEnvAware config transformation
     *
     * @param <R>
     * @param func - transformation function
     * @return new MultiEnvAware instance
     */
    public <R> MultiEnvAware<R> convert(BiFunction<String, T, R> func) {
        return convert(func, null);
    }

    /**
     * Utility method for MultiEnvAware config transformation
     *
     * @param <R>
     * @param func - transformation function
     * @param tr The template resolver to use
     * @return new MultiEnvAware instance
     */
    @SuppressWarnings("unchecked")
    public <R> MultiEnvAware<R> convert(BiFunction<String, T, R> func, TemplateResolver tr) {
        MultiEnvAware<R> result;
        if (this.hasTemplateEnvironment() && tr != null) {
            result = new LazyMultiEnvAware<>(tr);
        } else {
            result = new MultiEnvAware<>();
        }
        for (Entry<String, T> entry : map.entrySet()) {
            R r = func.apply(entry.getKey(), entry.getValue());
            result.put(entry.getKey(), r);
        }
        result.defaultEnvironment = this.defaultEnvironment;
        result.templateEnvironment = this.templateEnvironment;
        return result;
    }

    /**
     * @return {@code true} if default environment is configured for this instance. Only one default environment is
     * allowed per instance.
     */
    public boolean hasDefaultEnvironment() {
        return defaultEnvironment != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int size() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsKey(Object key) {
        return key != null && map.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * Returns the key, if that specified key is mapped to something
     *
     * @param key - environment name
     * @return String
     */
    public String getKeyOrDefault(String key) {
        if (StringUtils.isBlank(key)) {
            if (this.hasDefaultEnvironment()) {
                return defaultEnvironment;
            } else {
                throw new MultiEnvSupportException("[environment] property is mandatory and can't be empty");
            }
        }
        if (map.containsKey(key)) {
            return key;
        }
        if (this.hasTemplateEnvironment()) {
            return this.templateEnvironment;
        }
        LOG.error("Failed to find environment [{}] in {}", key, this.keySet());
        throw new MultiEnvSupportException(String.format(
                "Failed to find configuration for environment %s", key));
    }

    /**
     * Shortcut to provide default configuration key.
     *
     * @return String
     */
    public String getDefaultKey() {
        if (this.hasDefaultEnvironment()) {
            return getKeyOrDefault(null);
        }
        throw new MultiEnvSupportException("This instance has no default environment configuration");
    }

    /**
     * Shortcut to provide template configuration key.
     *
     * @return String
     */
    public String getTemplateKey() {
        if (this.hasTemplateEnvironment()) {
            return templateEnvironment;
        }
        throw new MultiEnvSupportException("This instance has no template environment configuration");
    }

    /**
     * Get the template environment if it exists or null
     *
     * @return The template value T or null if it doesn't exist
     */
    public T getTemplate() {
        return map.get(this.templateEnvironment);
    }

    /**
     * Returns the value to which the specified key is mapped, and adds it to the internal map if it wasn't previously
     * present.
     *
     * @param key - environment name
     */
    @Override
    public T get(Object key) {
        String sKey = (String) key;
        if (StringUtils.isBlank(sKey) && this.hasDefaultEnvironment()) {
            sKey = defaultEnvironment;
        } else if (StringUtils.isBlank(sKey)) {
            throw new MultiEnvSupportException("[environment] property is mandatory and can't be empty");
        }
        T value = map.get(sKey);
        if (value == null) {
            if (creationFunction != null) {
                value = creationFunction.apply(sKey);
            } else {
                value = resolve(sKey, getTemplate());
            }

            if (value != null) {
                map.put(sKey, value);
            }
        }

        return value;
    }

    /**
     * An internal method that allows us to add directly to the internal map
     * 
     * @param sKey
     * @param value 
     */
    protected void addInternalMap(String sKey, T value) {
        map.put(sKey, value);
    }
    
    /**
     * Shortcut to provide default configuration value.
     *
     * @return T where T is the value of the default environment if configured
     */
    public T get() {
        if (this.hasDefaultEnvironment()) {
            return get(null);
        }
        throw new MultiEnvSupportException("This instance has no default environment configuration");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T put(String key, T value) {
        if (StringUtils.isBlank(key)) {
            throw new MultiEnvSupportException(String.format("Expected non-empty value for environment, was: %s", key));
        }
        boolean isDefaultEnvironment = false;
        if (value instanceof MultiEnvDefaultedConfiguration) {
            isDefaultEnvironment = ((MultiEnvDefaultedConfiguration) value).isDefault();
            if (isDefaultEnvironment && this.hasDefaultEnvironment()) {
                throw new MultiEnvSupportException(String.format(
                        "Only one default environment is allowed per instance. Found %s and %s",
                        key, defaultEnvironment));
            } else if (isDefaultEnvironment) {
                this.defaultEnvironment = key;
            }
        }
        if (value instanceof MultiEnvTemplateConfiguration) {
            boolean isTemplateEnvironment = ((MultiEnvTemplateConfiguration) value).isTemplate();
            if (isTemplateEnvironment && this.hasTemplateEnvironment()) {
                throw new MultiEnvSupportException(String.format(
                        "Only one template environment is allowed per instance. Found %s and %s",
                        key, templateEnvironment));
            } else if (isTemplateEnvironment && isDefaultEnvironment) {
                throw new MultiEnvSupportException(String.format(
                        "You cannot have a configuration be a default and a template at the same time. Found %s", key));
            } else if (isTemplateEnvironment) {
                this.templateEnvironment = key;
            }
        }
        return map.put(key, value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T remove(Object key) {
        return map.remove(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        map.putAll(m);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void clear() {
        map.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<T> values() {
        return map.values();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entry<String, T>> entrySet() {
        return map.entrySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return map.toString();
    }

    /**
     * @return {@code true} if template environment is configured for this instance. Only one template environment is
     * allowed per instance.
     */
    private boolean hasTemplateEnvironment() {
        return !templateEnvironment.equals(DEFAULT_TEMPLATE_ENVIRONMENT_NAME);
    }

    /**
     * By default we don't try to resolve anything, but others can extend this behavior if they'd like to try to resolve
     * not found values differently.
     * 
     * @param sKey
     * @param value
     * @return 
     */
    protected T resolve(String sKey, T value) {
        LOG.error("Fail to find environment [{}] in {}", sKey, this.keySet());
        throw new MultiEnvSupportException(String.format(
                "Fail to find configuration for environment %s", sKey));
    }

    /**
     * Assign a creation function for this instance - this function should be responsible for creating new values for
     * environments which have not yet been added to this instance and can't (or don't) have templates.
     *
     * @param func
     */
    public void setCreationFunction(Function<String, T> func) {
        this.creationFunction = func;
    }


}
