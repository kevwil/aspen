package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackEnvironment;
import com.github.kevwil.aspen.RackUtil;
import com.github.kevwil.aspen.exception.ServiceException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jruby.Ruby;
import org.jruby.RubyHash;

import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * @author kevwil
 * @since Dec 20, 2010
 */
public class Request
{
    public static final String METHOD_OVERRIDE_PARAMETER = "_method";
    public static final String METHOD_OVERRIDE_HEADER = "X-Http-Method-Override";
    private final ChannelHandlerContext _context;
    private final HttpRequest _request;
    private HttpMethod _realMethod;
    private URL _url;
    private String _uri;
    private RubyHash _rubyHeaders;
    private final Ruby _runtime;
    private static final Object _lock = new Object();

    public Request( final ChannelHandlerContext context, final HttpRequest request, final Ruby runtime )
    {
        _context = context;
        _request = request;
        _runtime = runtime;
        initialize();
    }

    private void initialize()
    {
        _uri = _request.getUri();
        _realMethod = parseRealMethod( parseQueryStringParams() );
        _url = parseUrl();
        _rubyHeaders = RubyHash.newHash( _runtime );
        RackUtil.parseHeaders( _context, _request, _rubyHeaders );
    }

    public Ruby getRuntime()
    {
        synchronized( _lock )
        {
            return _runtime;
        }
    }

    public URL getUrl()
    {
        return _url;
    }

    public RubyHash getRubyHeaders()
    {
        return _rubyHeaders;
    }

    public HttpRequest getHttpRequest()
    {
        return _request;
    }

    public RackEnvironment getEnv()
    {
        synchronized( _lock )
        {
            return new DefaultRackEnvironment( _runtime, this );
        }
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

    public String getBodyString()
    {
        return getBody().toString( Charset.forName( "UTF-8" ) );
    }

    public void setBody( ChannelBuffer body )
    {
        _request.setContent( body );
    }

    public boolean containsHeader( String name )
    {
        return _request.containsHeader( name );
    }

    public String getHeader( String name )
    {
        return _request.getHeader( name );
    }

    public String getUri()
    {
        return _uri;
    }

    public SocketAddress getRemoteAddress()
    {
        return _context.getChannel().getRemoteAddress();
    }

    public SocketAddress getLocalAddress()
    {
        return _context.getChannel().getLocalAddress();
    }

    public boolean isKeepAlive()
    {
        return HttpHeaders.isKeepAlive( _request );
    }

    private URL parseUrl()
    {
        URL result;
        try
        {
            result = new URL( _uri );
        }
        catch( MalformedURLException e )
        {
            InetSocketAddress local = (InetSocketAddress) getLocalAddress();
            StringBuilder sb = new StringBuilder();
            sb.append( getProtocolFromLocalAddress( local ) )
                    .append( local.getHostName() )
                    .append( getPortFromLocalAddress( local ) )
                    .append( _request.getUri() );
            try
            {
                result = new URL( sb.toString() );
            }
            catch( MalformedURLException mue )
            {
                throw new ServiceException( mue );
            }
        }
        return result;
    }

    private static String getProtocolFromLocalAddress( final InetSocketAddress local )
    {
        return ( local.getPort() == 443 ? "https://" : "http://" );
    }

    private static String getPortFromLocalAddress( final InetSocketAddress local )
    {
        switch( local.getPort() )
        {
            case 80: return "";
            case 443: return "";
            default: return ":" + local.getPort();
        }
    }

    private HttpMethod parseRealMethod( Map<String,String> qs )
    {
        if( ! HttpMethod.POST.equals( _request.getMethod() ) )
            return _request.getMethod();

        if( _request.containsHeader( Request.METHOD_OVERRIDE_HEADER ) )
        {
            return HttpMethod.valueOf( _request.getHeader( Request.METHOD_OVERRIDE_HEADER ) );
        }
        if( qs.containsKey( Request.METHOD_OVERRIDE_PARAMETER ) )
        {
            String method = qs.get( Request.METHOD_OVERRIDE_PARAMETER );
            _request.addHeader( Request.METHOD_OVERRIDE_HEADER, method );
            return HttpMethod.valueOf( method );
        }
        return _request.getMethod();
    }

    private Map<String, String> parseQueryStringParams()
    {
        Map<String,String> params = new HashMap<String,String>();
        int q = _uri.indexOf( "?" );
        String qs = ( q >= 0 ? _uri.substring( q+1 ) : null );
        if( qs != null )
        {
            String[] pairs = qs.split( "&" );
            for( String pair : pairs )
            {
                String[] kv = pair.split( "=" );
                String value = ( kv.length > 1 ? kv[1] : "" );
                params.put( kv[0], value );
            }
        }
        return params;
    }
}
