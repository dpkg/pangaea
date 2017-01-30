package com.cvent.pangaea;

/**
 * Support multi environment configuration with template instance.
 * 
 * The purpose of this is to mark configurations that support which one will be the template to use if an
 * environment is specified that's not currently defined we can fallback to the template configuration with
 * some dynamic substitution.
 *
 * 
 * For example, XXX could be replaced dynamically based on some convention
 * environmentConfig:
 *     template:
 *         <<: *default_env_config
 *         database:
 *             <<: *default_db
 *             url: "jdbc:log4jdbc:sqlserver://a1-dba-XXX.a1.cvent.com\\dev_silo;database=CVENT_PROD"
 *         template: true
 * 
 */
public interface MultiEnvTemplateConfiguration {

    /**
     * @return {@code true} in case this environment is template.
     * Only one template environment is allowed per instance.
     */
    boolean isTemplate();
}
