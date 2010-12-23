package com.github.kevwil.aspen.domain;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Map.Entry;

/**
 * @author kevwil
 * @since Dec 20, 2010
 */
public class Request
{
    private static final String METHOD_OVERRIDE_PARAMETER = "_method";
    private static final String METHOD_OVERRIDE_HEADER = "X-Http-Method-Override";
    private HttpRequest _request;
    private HttpMethod _realMethod;
    private Map<String,String> _qsParams;
    private List<Entry<String,String>> _originalHeaders;

    public Request( HttpRequest request )
    {
        _request = request;
        _originalHeaders = request.getHeaders();
        _qsParams = parseQueryStringParams( request );
        _realMethod = parseRealMethod( request );
    }

    public HttpMethod getMethod()
    {
        return _request.getMethod();
    }

    public HttpMethod getRealMethod()
    {
        return _realMethod;
    }

    public ChannelBuffer getBody()
    {
        return _request.getContent();
    }

    public void setBody( ChannelBuffer body )
    {
        _request.setContent( body );
    }

    public void clearHeaders()
    {
        _request.clearHeaders();
    }

    public boolean containsHeader( String name )
    {
        return _request.containsHeader( name );
    }

    public void addHeader( String name, String value )
    {
        _request.addHeader( name, value );
    }

    public void removeHeader( String name )
    {
        _request.removeHeader( name );
    }

    public String getUri()
    {
        return _request.getUri();
    }

    public boolean isKeepAlive()
    {
        return HttpHeaders.isKeepAlive( _request );
    }

    public boolean is100ContinueExpected()
    {
        return HttpHeaders.is100ContinueExpected( _request );
    }

    public boolean isChunked()
    {
        return _request.isChunked();
    }

    public Map<String,String> getQueryStringParams()
    {
        return _qsParams;
    }

    public List<Entry<String,String>> getOriginalHeaders()
    {
        return _originalHeaders;
    }

    private Map<String, String> parseQueryStringParams( HttpRequest request )
    {
        Map<String,String> params = new HashMap<String,String>();
        String uri = getUri( request );
        int q = uri.indexOf( "?" );
        String qs = ( q >= 0 ? uri.substring( q+1 ) : null );
        if( qs != null )
        {
            String[] pairs = qs.split( "&" );
            for( String pair : pairs )
            {
                String[] kv = pair.split( "=" );
                String value = ( kv.length > 1 ? kv[1] : "" );
                if( kv[0].equals( METHOD_OVERRIDE_PARAMETER ) )
                {
                    request.addHeader( METHOD_OVERRIDE_HEADER, value );
                }
                else
                {
                    request.addHeader( kv[0], value );
                }
                params.put( kv[0], value );
            }
        }
        return params;
    }

    private HttpMethod parseRealMethod( HttpRequest request )
    {
        if( ! HttpMethod.POST.equals( request.getMethod() ) )
            return request.getMethod();

        if( request.containsHeader( METHOD_OVERRIDE_HEADER ) )
        {
            return HttpMethod.valueOf( request.getHeader( METHOD_OVERRIDE_HEADER ) );
        }
        return _request.getMethod();
    }

    private String getUri( HttpRequest request )
    {
        String result = "";
        try
        {
            result = URLDecoder.decode( request.getUri(), "UTF-8" );
        }
        catch( UnsupportedEncodingException e )
        {
        }
        return result;
    }
}
