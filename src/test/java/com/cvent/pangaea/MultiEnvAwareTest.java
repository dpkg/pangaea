package com.cvent.pangaea;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;


import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Test code for MultiEnvAware
 * 
 * @author bryan
 */
public class MultiEnvAwareTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private MultiEnvAware<MultiEnvConfig> unit;

    @Before
    public void setup() {
        unit = new MultiEnvAware<>();
    }

    @Test
    public void testGetValue_nullKey_multiEnvException() {
        exception.expect(MultiEnvSupportException.class);
        exception.expectMessage("property is mandatory");
        unit.get(null);
    }

    @Test
    public void testGetValue_enptyKey_multiEnvException() {
        exception.expect(MultiEnvSupportException.class);
        exception.expectMessage("property is mandatory");
        unit.get("");
    }

    @Test
    public void testGetValue_unknownEnvironment_multiEnvException() {
        exception.expect(MultiEnvSupportException.class);
        exception.expectMessage("Fail to find configuration for environment");
        unit.get("environment_name");
    }

    @Test
    public void testGetValue_unknownEnvironmentWithTemplate_multiEnvException() {
        MultiEnvConfig template = new MultiEnvConfig();
        template.setTemplate(true);
        unit.put("template", template);

        MultiEnvAware<MultiEnvConfig> convertedUnit = unit.convert((env, conf) -> conf,
                new SiloTemplateResolver<>(MultiEnvConfig.class, new ObjectMapper()));
        MultiEnvConfig result = convertedUnit.get("S999");
        assertThat(result.surveyUrlDefaultDomain, is("a1-999-dba.a1.cvent.com"));
        assertThat(result.isTemplate(), is(true));
    }

    @Test
    public void testGetValue_unknownEnvironmentWithTemplateUsingExampleConfiguration_multiEnvException() throws
            Exception {
        ExampleConfiguration config = new ExampleConfiguration();
        MultiEnvAware<ExampleConfiguration.MultiEnvConfig> multiEnvAware = new MultiEnvAware<>();
        ExampleConfiguration.MultiEnvConfig template = new ExampleConfiguration.MultiEnvConfig();
        template.setTemplate(true);
        template.setSurveyUrlDefaultDomain("a1-XXX-dba.a1.cvent.com");
        multiEnvAware.put("template", template);
        config.setEnvironmentConfig(multiEnvAware);

        ObjectMapper mapper = new ObjectMapper();
        //Convert to json so we can test deserialization like startup of a configuration class would use this
        String json = mapper.writeValueAsString(config);

        config = mapper.readValue(json, ExampleConfiguration.class);

        ExampleConfiguration.MultiEnvConfig result = config.getEnvironmentConfig().get("S999");
        assertThat(result.getSurveyUrlDefaultDomain(), is("a1-999-dba.a1.cvent.com"));
        assertThat(result.isTemplate(), is(true));
    }

    @Test
    public void testGetValue_returnConfigByEnvironmentName() {
        MultiEnvConfig value = new MultiEnvConfig();
        String[] environments = {"env1", "env2", "env3"};
        for (String environment : environments) {
            unit.put(environment, value);
        }
        for (String environment : environments) {
            assertThat(unit.get(environment), is(value));
        }
    }

    @Test
    public void testConvert_emptyInput_emptyReuslt() {
        assertThat(unit.size(), is(0));
        MultiEnvAware<String> result = unit.convert((env, val) -> val.toString());
        assertThat(result.size(), is(0));
    }

    @Test
    public void testConvert_invokeTransformFunction() {
        String environment = "environment";
        MultiEnvConfig value = new MultiEnvConfig();
        unit.put(environment, value);

        MultiEnvAware<String> convert = unit.convert((env, val) -> env + "_" + val.toString());
        String result = convert.get(environment);
        assertThat(result, notNullValue());
        assertThat(result, is("environment_" + value.toString()));
    }

    @Test
    public void testGetValue_caseSensitiveEnvLookup() {
        MultiEnvConfig one = new MultiEnvConfig();
        MultiEnvConfig two = new MultiEnvConfig();

        unit.put("env", one);
        unit.put("ENV", two);

        assertThat(unit.get("env"), is(one));
        assertThat(unit.get("ENV"), is(two));
    }

    @Test
    public void testWithDefaultedConfiguration() {
        MultiEnvConfig c0 = new MultiEnvConfig();
        c0.setDefaultEnvironmentConfiguration(false);
        MultiEnvConfig c1 = new MultiEnvConfig();
        c1.setDefaultEnvironmentConfiguration(true);
        MultiEnvConfig c2 = new MultiEnvConfig();
        c2.setDefaultEnvironmentConfiguration(false);

        unit.put("0", c0);
        unit.put("1", c1);
        unit.put("4", c2);

        assertThat(unit.hasDefaultEnvironment(), is(true));
        for (String env : new String[]{"1", "", " ", null}) {
            assertThat(unit.get(env), is(c1));
        }
        assertThat(unit.get(), is(c1));
    }

    @Test
    public void testWithDefaultedConfiguration_permitSingleDefaultValue() {
        exception.expect(MultiEnvSupportException.class);
        exception.expectMessage("Only one default environment is allowed");
        MultiEnvConfig c1 = new MultiEnvConfig();
        c1.setDefaultEnvironmentConfiguration(true);
        unit.put("key1", c1);
        unit.put("key2", c1);
    }

    @Test
    public void testConvertWithDefaultedEnvironment() {
        MultiEnvConfig c1 = new MultiEnvConfig();
        c1.setDefaultEnvironmentConfiguration(true);

        unit.put("1", c1);
        unit.put("2", new MultiEnvConfig());

        MultiEnvAware<String> convert = unit.convert((env, val) -> env + "_" + val.toString());
        assertThat(convert.hasDefaultEnvironment(), is(true));
        assertThat(convert.get(), is("1_DEFAULT"));
    }

    @Test
    public void testContainsKey() {
        // First make sure keys in map can be found
        unit.put("key1", new MultiEnvConfig());
        assertThat(unit.containsKey("key1"), is(true));

        // Then verify null key should returns false but no exception
        assertThat(unit.containsKey(null), is(false));

        // Last verify key not in the map should returns false but no exception
        assertThat(unit.containsKey("key2"), is(false));
    }

    /**
     * Test class for multi env configs
     */
    private static class MultiEnvConfig extends BaseEnvironmentConfiguration {

        @JsonProperty
        private String surveyUrlDefaultDomain = "a1-XXX-dba.a1.cvent.com";

        @Override
        public String toString() {
            return "DEFAULT";
        }

        public String getSurveyUrlDefaultDomain() {
            return surveyUrlDefaultDomain;
        }
    }
}
