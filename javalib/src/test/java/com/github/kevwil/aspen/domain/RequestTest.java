package com.github.kevwil.aspen.domain;

import org.jboss.netty.handler.codec.http.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author kevinw
 * @since Dec 21, 2010
 */
public class RequestTest
{
    private HttpRequest httpRequest;
    private Request req;

    @Test
    public void shouldBuildBasicRequest()
            throws Exception
    {
        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "/" );
        req = new Request( httpRequest );

        assertEquals( HttpMethod.GET, req.getMethod() );
        assertEquals( HttpMethod.GET, req.getRealMethod() );
        assertEquals( 0, req.getBody().toByteBuffer().capacity() );
        assertEquals( "/", req.getUri() );
    }

    @Test
    public void shouldHandleMethodOverride()
    {
        QueryStringEncoder encoder = new QueryStringEncoder( "/" );
        encoder.addParam( "foo", "bar" );
        String path = encoder.toString();

        httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.POST, path );
        httpRequest.addHeader( "X-Http-Method-Override", "PUT" );
        req = new Request( httpRequest );

        assertEquals( HttpMethod.POST, req.getMethod() );
        assertEquals( HttpMethod.PUT, req.getRealMethod() );
        assertTrue( req.containsHeader( "foo" ) );
    }
}
