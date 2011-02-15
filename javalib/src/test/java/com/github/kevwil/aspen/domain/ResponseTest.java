package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackUtil;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Dec 21, 2010
 */
public class ResponseTest
{

    @SuppressWarnings( { "ThrowableResultOfMethodCallIgnored" } )
    @Test
    public void shouldBuildDefaultParams()
    {
        HttpRequest hr = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        ChannelHandlerContext ctx = RackUtil.buildDummyChannelHandlerContext( "localhost", "80" );
        Request req = new Request( ctx, hr, Ruby.getGlobalRuntime() );
        Response resp = new Response( req );
        assertNotNull( resp );
        assertNull( resp.getBody() );
        assertNull( resp.getException() );
        assertTrue( resp.getHeaderNames().isEmpty() );
        assertEquals( req, resp.getRequest() );
    }
}
