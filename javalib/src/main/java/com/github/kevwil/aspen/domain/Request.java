package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackEnvironment;
import com.github.kevwil.aspen.RackUtil;
import com.github.kevwil.aspen.exception.ServiceException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.jruby.RubyHash;

import java.net.*;
import java.nio.charset.StandardCharsets;
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
    private FullHttpRequest _request;
    private HttpMethod _realMethod;
    private URL _url;
    private String _uri;
    private RubyHash _rubyHeaders;
    private final Ruby _runtime;
    private static final Object _lock = new Object();

    public Request(final ChannelHandlerContext context, final FullHttpRequest request, final Ruby runtime )
    {
        _context = context;
        _request = request;
        _runtime = runtime;
        initialize();
    }

    private void initialize()
    {
        _uri = _request.uri();
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

    public FullHttpRequest getHttpRequest()
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
        return _request.method();
    }

    public HttpMethod getRealMethod()
    {
        return _realMethod;
    }

    public ByteBuf getBody()
    {
        return _request.content();
    }

    public String getBodyString()
    {
        return getBody().toString( StandardCharsets.UTF_8 );
    }

    public void setBody( ByteBuf body )
    {
        _request = _request.replace(body);
    }

    public boolean containsHeader( String name )
    {
        return _request.headers().contains( name );
    }

    public String getHeader( String name )
    {
        return _request.headers().get( name );
    }

    public String getUri()
    {
        return _uri;
    }

    public SocketAddress getRemoteAddress()
    {
        return _context.channel().remoteAddress();
    }

    public SocketAddress getLocalAddress()
    {
        return _context.channel().localAddress();
    }

    public boolean isKeepAlive()
    {
        return HttpUtil.isKeepAlive( _request );
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
                    .append( _request.uri() );
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
            case 80:
            case 443: return "";
            default: return ":" + local.getPort();
        }
    }

    private HttpMethod parseRealMethod( Map<String,String> qs )
    {
        if( ! HttpMethod.POST.equals( _request.method() ) )
            return _request.method();

        if( _request.headers().contains( Request.METHOD_OVERRIDE_HEADER ) )
        {
            return HttpMethod.valueOf( _request.headers().get( Request.METHOD_OVERRIDE_HEADER ) );
        }
        if( qs.containsKey( Request.METHOD_OVERRIDE_PARAMETER ) )
        {
            String method = qs.get( Request.METHOD_OVERRIDE_PARAMETER );
            _request.headers().add( Request.METHOD_OVERRIDE_HEADER, method );
            return HttpMethod.valueOf( method );
        }
        return _request.method();
    }

    private Map<String, String> parseQueryStringParams()
    {
        Map<String,String> params = new HashMap<>();
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
