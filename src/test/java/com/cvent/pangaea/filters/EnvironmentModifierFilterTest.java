package com.cvent.pangaea.filters;

import com.cvent.pangaea.filter.EnvironmentModifierFilter;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author rashi.gupta
 *
 * Tests for EnvironmentModifierFilter
 */
public class EnvironmentModifierFilterTest {

    private ContainerRequestContext containerRequestContext;
    
    @Before
    public void initialize() throws Exception {
        containerRequestContext = mock(ContainerRequestContext.class);
    }

    @Test
    public void testModifyFilter() throws IOException, Exception {      
        UriInfo info = mock(UriInfo.class);
        MultivaluedMap map = mock(MultivaluedMap.class);
        when(map.isEmpty()).thenReturn(Boolean.FALSE);
        when(map.containsKey("environment")).thenReturn(Boolean.TRUE);
        when(map.getFirst("environment")).thenReturn("P2");
        when(info.getQueryParameters()).thenReturn(map);
        when(info.getBaseUri()).thenReturn(URI.create("http://www.cvent.com/"));
        when(info.getRequestUri()).thenReturn(URI.create("http://www.cvent.com?environment=production"));
        when(containerRequestContext.getUriInfo()).thenReturn(info);
        EnvironmentModifierFilter environmentModifierFilter = new EnvironmentModifierFilter("P2", "production");
        environmentModifierFilter.filter(containerRequestContext);

        assertEquals("P2", containerRequestContext.getUriInfo().getQueryParameters().getFirst("environment"));
    }

    @Test
    public void testShouldNotModifyEnvironment() throws IOException, Exception {
        UriInfo info = mock(UriInfo.class);
        MultivaluedMap map = mock(MultivaluedMap.class);
        when(map.isEmpty()).thenReturn(Boolean.FALSE);
        when(map.containsKey("environment")).thenReturn(Boolean.TRUE);
        when(map.getFirst("environment")).thenReturn("staging");
        when(info.getQueryParameters()).thenReturn(map);
        when(info.getBaseUri()).thenReturn(URI.create("http://www.cvent.com/"));
        when(info.getRequestUri()).thenReturn(URI.create("http://www.cvent.com?environment=staging"));
        when(containerRequestContext.getUriInfo()).thenReturn(info);
        EnvironmentModifierFilter environmentModifierFilter = new EnvironmentModifierFilter("P2", "production");
        environmentModifierFilter.filter(containerRequestContext);

        assertEquals("staging", containerRequestContext.getUriInfo().getQueryParameters().getFirst("environment"));
    }
}
