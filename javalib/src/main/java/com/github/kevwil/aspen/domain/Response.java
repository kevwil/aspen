package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RubyUtil;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.jruby.RubyHash;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.*;

/**
 * @author kevwil
 * @since Dec 21, 2010
 */
public class Response
{
    private HttpResponseStatus statusCode = HttpResponseStatus.OK;
    private Throwable exception = null;
    private Object body;
    private final Map<String,List<String>> headers = new HashMap<>();
    private final Request req;

    public Response( Request request )
    {
        req = request;
    }

    protected Request getRequest()
    {
        return req;
    }

    public Object getBody()
    {
        return body;
    }

    public void setBody( Object body )
    {
        this.body = body;
    }

    public boolean hasBody()
    {
        return ( getBody() != null );
    }

//    public void clearHeaders()
//    {
//        _headers.clear();
//    }

//    public String getHeader( String name )
//    {
//        List<String> values =_headers.get( name );
//        if( values != null && !values.isEmpty() )
//        {
//            return values.get( 0 );
//        }
//        return null;
//    }

    public List<String> getHeaders( String name )
    {
        return headers.get( name );
    }

    public Set<String> getHeaderNames()
    {
        return headers.keySet();
    }

    public void addHeader( String name, String value )
    {
        List<String> values = headers.get( name );
        if( values == null )
        {
            values = new ArrayList<>();
        }
        values.add( value );
        headers.put( name, values );
    }

    public void setResponseCode( int code )
    {
        statusCode = HttpResponseStatus.valueOf( code );
    }

//    public void setResponseStatus( HttpResponseStatus responseStatus )
//    {
//        _statusCode = responseStatus;
//    }

//    public void setResponseCreated()
//    {
//        _statusCode = HttpResponseStatus.CREATED;
//    }

//    public void setResponseNoContent()
//    {
//        _statusCode = HttpResponseStatus.NO_CONTENT;
//    }

    public HttpResponseStatus getResponseStatus()
    {
        return statusCode;
    }

    public Throwable getException()
    {
        return exception;
    }

    public boolean hasException()
    {
        return ( exception != null );
    }

    public void setException( Throwable e )
    {
        exception = e;
    }

    public void addHeaders( final RubyHash headers )
    {
        for( IRubyObject key : headers.keys().toJavaArray() )
        {
            IRubyObject value = RubyUtil.hashGet( headers, key );
            // if hash value
            if(value.getClass().isAssignableFrom(headers.getClass()))
//            if( JavaClass.assignable( value.getClass(), headers.getClass() ) )
            {
                RubyHash valueHash = (RubyHash)value;
                for( IRubyObject key1 : valueHash.keys().toJavaArray() )
                {
                    addHeader( key.toString(), RubyUtil.hashGet( valueHash, key1 ).toString() );
                }
            }
            else
            {
                addHeader( key.toString(), value.toString() );
            }
        }
    }
}
