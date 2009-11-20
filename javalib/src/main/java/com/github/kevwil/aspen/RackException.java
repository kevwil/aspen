package com.github.kevwil.aspen;

/**
 * @author kevwil
 * @since Oct 4, 2009
 */
public class RackException extends Exception
{
    public RackException()
    {
    }

    RackException( final String message )
    {
        super( message );
    }

    public RackException( final String message, final Throwable cause )
    {
        super( message, cause );
    }

    public RackException( final Throwable cause )
    {
        super( cause );
    }
}
