package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RubyUtil;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jruby.RubyHash;
import org.jruby.javasupport.JavaClass;
import org.jruby.runtime.builtin.IRubyObject;

import java.util.*;

/**
 * @author kevwil
 * @since Dec 21, 2010
 */
public class Response
{
    private HttpResponseStatus _statusCode = HttpResponseStatus.OK;
    private Throwable _exception = null;
    private Object _body;
    private Map<String,List<String>> _headers = new HashMap<String,List<String>>();
    private Request _req;

    public Response( Request request )
    {
        _req = request;
    }

    protected Request getRequest()
    {
        return _req;
    }

    public Object getBody()
    {
        return _body;
    }

    public void setBody( Object body )
    {
        _body = body;
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
        return _headers.get( name );
    }

    public Set<String> getHeaderNames()
    {
        return _headers.keySet();
    }

    public void addHeader( String name, String value )
    {
        List<String> values = _headers.get( name );
        if( values == null )
        {
            values = new ArrayList<String>();
        }
        values.add( value );
        _headers.put( name, values );
    }

    public void setResponseCode( int code )
    {
        _statusCode = HttpResponseStatus.valueOf( code );
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
        return _statusCode;
    }

    public Throwable getException()
    {
        return _exception;
    }

    public boolean hasException()
    {
        return ( _exception != null );
    }

    public void setException( Throwable e )
    {
        _exception = e;
    }

    public void addHeaders( final RubyHash headers )
    {
        for( IRubyObject key : headers.keys().toJavaArray() )
        {
            IRubyObject value = RubyUtil.hashGet( headers, key );
            // if hash value
            if( JavaClass.assignable( value.getClass(), headers.getClass() ) )
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
