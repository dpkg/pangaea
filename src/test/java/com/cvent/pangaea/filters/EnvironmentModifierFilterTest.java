package com.cvent.pangaea.filters;

import com.cvent.pangaea.filter.EnvironmentModifierFilter;
import com.sun.jersey.core.header.InBoundHeaders;
import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.WebApplication;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.RETURNS_MOCKS;
import static org.mockito.Mockito.mock;

/**
 * @author rashi.gupta
 *
 * Tests for EnvironmentModifierFilter
 */
public class EnvironmentModifierFilterTest {

    WebApplication webApplication;

    @Before
    public void initialize() throws Exception {
        webApplication = mock(WebApplication.class, RETURNS_MOCKS);
    }

    @Test
    public void testModifyFilter() throws IOException, Exception {
        ContainerRequest containerRequest = new ContainerRequest(webApplication, "GET",
                URI.create("http://www.cvent.com/"), URI.create("http://www.cvent.com?environment=production"),
                new InBoundHeaders(),new ByteArrayInputStream(new byte[0]));
        EnvironmentModifierFilter environmentModifierFilter = new EnvironmentModifierFilter("P2", "production");
        ContainerRequest modifiedRequest = environmentModifierFilter.filter(containerRequest);

        assertEquals(modifiedRequest.getQueryParameters().getFirst("environment"), "P2");
    }

    @Test
    public void testShouldNotModifyEnvironment() throws IOException, Exception {
        ContainerRequest containerRequest = new ContainerRequest(webApplication, "GET",
                URI.create("http://www.cvent.com/"), URI.create("http://www.cvent.com?environment=staging"),
                new InBoundHeaders(),new ByteArrayInputStream(new byte[0]));
        EnvironmentModifierFilter environmentModifierFilter = new EnvironmentModifierFilter("P2", "production");
        ContainerRequest modifiedRequest = environmentModifierFilter.filter(containerRequest);

        assertEquals(modifiedRequest.getQueryParameters().getFirst("environment"), "staging");
    }
}
