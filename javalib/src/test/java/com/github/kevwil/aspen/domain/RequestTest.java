package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackEnvironment;
import com.github.kevwil.aspen.RackUtil;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author kevinw
 * @since Dec 21, 2010
 */
public class RequestTest
{
    private HttpRequest httpRequest;
    private Request req;
    private ChannelHandlerContext ctx;

    @Before
    public void setUp()
    {
        ctx = RackUtil.buildDummyChannelHandlerContext( "localhost", "80" );
    }

    @Test
    public void shouldBuildBasicRequest()
            throws Exception
    {
        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        req = new Request( ctx, httpRequest );

        assertEquals( HttpMethod.GET, req.getMethod() );
        assertEquals( HttpMethod.GET, req.getRealMethod() );
        assertEquals( 0, req.getBody().toByteBuffer().capacity() );
        assertEquals( "http://localhost/", req.getUri() );
        assertEquals( "http://localhost/", req.getUrl().toString() );
    }

    @Test
    public void shouldBuildRequestWithOnlyUri()
    {
        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "/" );
        req = new Request( ctx, httpRequest );

        assertEquals( HttpMethod.GET, req.getMethod() );
        assertEquals( HttpMethod.GET, req.getRealMethod() );
        assertEquals( 0, req.getBody().toByteBuffer().capacity() );
        assertEquals( "/", req.getUri() );
        assertEquals( "http://localhost/", req.getUrl().toString() );
    }

    @Test
    public void shouldHandleMethodOverrideFromQueryString()
    {
        QueryStringEncoder encoder = new QueryStringEncoder( "/" );
        encoder.addParam( "foo", "bar" );
        encoder.addParam( Request.METHOD_OVERRIDE_PARAMETER, "PUT" );
        String path = encoder.toString();

        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost/"+path );
        req = new Request( ctx, httpRequest );

        assertEquals( HttpMethod.POST, req.getMethod() );
        assertEquals( HttpMethod.PUT, req.getRealMethod() );
        assertTrue( req.getUri().contains( "foo" ) );
        assertTrue( req.containsHeader( Request.METHOD_OVERRIDE_HEADER ) );
    }

    @Test
    public void shouldHandleMethodOverrideFromHeader()
    {
        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost/foo" );
        httpRequest.addHeader( "X-Http-Method-Override", "PUT" );
        req = new Request( ctx, httpRequest );

        assertEquals( HttpMethod.POST, req.getMethod() );
        assertEquals( HttpMethod.PUT, req.getRealMethod() );
    }

    @Test
    public void shouldReturnAnEnvironmentWrapper()
    {
        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "/" );
        req = new Request( ctx, httpRequest );
        RackEnvironment env = req.getEnv();
        assertNotNull( env );
    }
}
