package com.github.kevwil.aspen.domain;

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
    public void shouldDoNothing()
    {
        req = new Request( new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "/" ) );
        resp = new Response( req );
        assertNotNull( resp );
        assertNull( resp.getBody() );
        assertNull( resp.getException() );
        assertTrue( resp.getHeaderNames().isEmpty() );
        assertEquals( req, resp.getRequest() );
    }
}
