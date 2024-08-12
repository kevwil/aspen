package com.github.kevwil.aspen.exception;

/**
 * @author kevwil
 * @since Jan 08, 2011
 */
public class InvalidAppException
extends RuntimeException
{
    private static final String DEFAULT_MESSAGE = "Invalid Application Signature";

    public InvalidAppException()
    {
        this( DEFAULT_MESSAGE );
    }

    public InvalidAppException( final String s )
    {
        super( s );
    }

    public InvalidAppException( final String s, final Throwable throwable )
    {
        super( s, throwable );
    }

    public InvalidAppException( final Throwable throwable )
    {
        this( DEFAULT_MESSAGE, throwable );
    }
}
