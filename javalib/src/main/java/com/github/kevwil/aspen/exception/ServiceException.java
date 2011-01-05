package com.github.kevwil.aspen.exception;

import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author kevwil
 * @since Jan 3, 2011
 */
public class ServiceException
extends RuntimeException
{
    private static final HttpResponseStatus STATUS = HttpResponseStatus.INTERNAL_SERVER_ERROR;
    private HttpResponseStatus _status;

    public ServiceException()
    {
        super();
        setStatus( STATUS );
    }

    public ServiceException( final HttpResponseStatus status )
    {
        super();
        setStatus( status );
    }

    public ServiceException( final String message )
    {
        this( message, STATUS );
    }

    protected ServiceException( final String message, final HttpResponseStatus status )
    {
        super( message );
        setStatus( status );
    }

    public ServiceException( final String message, final Throwable throwable )
    {
        this( message, throwable, STATUS );
    }

    protected ServiceException( final String message, final Throwable throwable, final HttpResponseStatus status )
    {
        super( message, throwable );
        setStatus( status );
    }

    public ServiceException( final Throwable throwable )
    {
        this( throwable, STATUS );
    }

    protected ServiceException( final Throwable throwable, final HttpResponseStatus status )
    {
        super( throwable );
        setStatus( status );
    }

    public HttpResponseStatus getStatus()
    {
        return _status;
    }

    public void setStatus( final HttpResponseStatus status )
    {
        _status = status;
    }
}
