package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackUtil;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Dec 21, 2010
 */
public class ResponseTest
{
    private Request req;
    private Response resp;

    @Test
    public void shouldBuildDefaultParams()
    {
        HttpRequest hr = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "/" );
        ChannelHandlerContext ctx = RackUtil.buildDummyChannelHandlerContext( "localhost", "80" );
        req = new Request( ctx, hr );
        resp = new Response( req );
        assertNotNull( resp );
        assertNull( resp.getBody() );
        assertNull( resp.getException() );
        assertTrue( resp.getHeaderNames().isEmpty() );
        assertEquals( req, resp.getRequest() );
    }
}
