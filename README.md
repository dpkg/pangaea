# Pangaea

pangaea is a common library for all dropwizard projects that need to deal with multiple environments.


# How do I use it?

Include the maven dependency in your project:

```
        <dependency>
            <groupId>com.cvent</groupId>
            <artifactId>pangaea</artifactId>
            <version>${pangaea.version}</version>
        </dependency>
```
# IDE Support

Any IDE supports maven so feel free to use Netbeans, IntelliJ, Eclipse

# How to build

mvn clean install

# How to deploy a SNAPSHOT

mvn clean deploy

# How to release

mvn --batch-mode clean release:prepare release:perform

#Pangaea package

this package is used for helping developers maintain multiple environments with a somewhat standard methology.

Add this to the top of your dropwizard configuration class so that this property doesn't try to get deserialized since it's only used as as a default section to be referenced by other sections of the configuration:

```
@JsonIgnoreProperties(value = {"default_multi_env_config"})
```

Add a configuration property to your configuration class that encapsulates the configurations that are "environment" specific:

```
    @JsonProperty
    @Valid
    @NotEmpty
    @NotNull
    private MultiEnvAware<MultiEnvConfig> environmentConfig;
```

Create your "multi-env" configuration class:

```
public class MultiEnvConfig extends BaseEnvironmentConfiguration {
}
```

Add configurations to your <env>.yaml files (example below):

```
default_multi_env_config: &default_env_config
    database: &default_db
        checkConnectionWhileIdle: true
        driverClass: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        initialSize: 5
        maxConnectionAge: "10s"
        minSize: 5
        maxSize: 5
        isLazy: true
        validationQuery: "SELECT 1"

# Map<environmentName, environmentConfig>
environmentConfig:
    S115:
        <<: *default_env_config
        database:
            <<: *default_db
            user: "user"
            password: "password"
            url: "jdbc:sqlserver://a.foo.com\\db:50000;database=DB"
        someUrl: "foo.cvent.com"
        defaultEnvironmentConfiguration: true
```
# Templates

Configuration templates in allow us to dynamically support applications for multiple environments
based on a convention (or pattern).

## Template Rules

* Only 1 template per configuration can be defined
* A configuration cannot have both defaultEnvironmentConfiguration=true and template=true.

## Template Classes

See main code for handling this

* TemplateResolver.java
* SiloTemplateResolver.java
* MultiEnvTemplateConfiguration.java
* LazyMultiEnvAware.java
* ExampleConfiguration.java

## Example

In this example, you'll see a template defined where XXX gets replaced dynamically as the application processes
requests for a given silo.  So if Silo 115 data gets passed through this configuration then XXX will get replaced
with 115 based on a convention.

```
default_multi_env_config: &default_env_config
    database: &default_db
        checkConnectionWhileIdle: true
        driverClass: "com.microsoft.sqlserver.jdbc.SQLServerDriver"
        initialSize: 5
        maxConnectionAge: "10s"
        minSize: 5
        maxSize: 5
        isLazy: true
        validationQuery: "SELECT 1"

# Map<environmentName, environmentConfig>
environmentConfig:
    template:
        <<: *default_env_config
        database:
            <<: *default_db
            user: "user"
            password: "password"
            url: "jdbc:sqlserver://foo.com\\db:50000;database=DB"
        someUrl: "foo.cvent.com"
        template: true
```