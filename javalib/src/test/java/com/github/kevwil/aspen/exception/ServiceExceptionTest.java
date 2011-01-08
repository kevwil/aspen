package com.github.kevwil.aspen.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author kevwil
 * @since Jan 04, 2011
 */
public class ServiceExceptionTest
{
    @Test
    public void shouldDefaultTo500()
    {
        ServiceException se = new ServiceException();
        assertNotNull( se.getStatus() );
        assertEquals( 500, se.getStatus().getCode() );
    }

    @Test
    public void shouldAssignMessage()
    {
        ServiceException se = new ServiceException( "foo" );
        assertNotNull( se.getMessage() );
        assertNotNull( se.getLocalizedMessage() );
        assertEquals( "foo", se.getLocalizedMessage() );
        assertEquals( se.getMessage(), se.getLocalizedMessage() );
    }

    @Test
    public void shouldAcceptStatusOverride()
    {
        ServiceException se = new ServiceException();
        se.setStatus( HttpResponseStatus.NOT_FOUND );
        assertNotNull( se.getStatus() );
        assertEquals( 404, se.getStatus().getCode() );
    }
}
