package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackEnvironment;
import com.github.kevwil.aspen.RackUtil;
import com.github.kevwil.aspen.exception.ServiceException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.jruby.RubyHash;

import java.io.IOException;
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
    private final ChannelHandlerContext context;
    private FullHttpRequest request;
    private HttpMethod realMethod;
    private URL url;
    private String uri;
    private RubyHash rubyHeaders;
    private final Ruby runtime;
    private static final Object LOCK = new Object();

    public Request(final ChannelHandlerContext context, final FullHttpRequest request, final Ruby runtime )
    {
        this.context = context;
        this.request = request;
        this.runtime = runtime;
        initialize();
    }

    private void initialize()
    {
        this.uri = request.uri();
        this.realMethod = parseRealMethod( parseQueryStringParams() );
        this.url = parseUrl();
        this.rubyHeaders = RubyHash.newHash(runtime);
        RackUtil.parseHeaders(context, request, rubyHeaders);
    }

    public Ruby getRuntime()
    {
        synchronized(LOCK)
        {
            return runtime;
        }
    }

    public URL getUrl()
    {
        return url;
    }

    public RubyHash getRubyHeaders()
    {
        return rubyHeaders;
    }

    public FullHttpRequest getHttpRequest()
    {
        return request;
    }

    public RackEnvironment getEnv()
    {
        synchronized(LOCK)
        {
            return new DefaultRackEnvironment(runtime, this );
        }
    }

    public HttpMethod getMethod()
    {
        return request.method();
    }

    public HttpMethod getRealMethod()
    {
        return realMethod;
    }

    public ByteBuf getBody()
    {
        return request.content();
    }

    public String getBodyString()
    {
        return getBody().toString( StandardCharsets.UTF_8 );
    }

    public void setBody( ByteBuf body )
    {
        request = request.replace(body);
    }

    public boolean containsHeader( String name )
    {
        return request.headers().contains( name );
    }

    public String getHeader( String name )
    {
        return request.headers().get( name );
    }

    public String getUri()
    {
        return uri;
    }

    public SocketAddress getRemoteAddress()
    {
        return context.channel().remoteAddress();
    }

    public SocketAddress getLocalAddress()
    {
        return context.channel().localAddress();
    }

    public boolean isKeepAlive()
    {
        return HttpUtil.isKeepAlive(request);
    }

    private URL parseUrl()
    {
        try {
            return new URI(uri).toURL();
        } catch (IllegalArgumentException | MalformedURLException | URISyntaxException e) {
//            throw new RuntimeException(e);
            return null;
        }
//        URL result;
//        try
//        {
//            result = new URL(uri);
//        }
//        catch( MalformedURLException e )
//        {
//            InetSocketAddress local = (InetSocketAddress) getLocalAddress();
//            StringBuilder sb = new StringBuilder();
//            sb.append(getProtocolFromLocalAddress(local))
//                    .append(local.getHostName())
//                    .append(getPortFromLocalAddress(local))
//                    .append(request.uri());
//            try {
//                result = new URL(sb.toString());
//            } catch (MalformedURLException mue) {
//                throw new ServiceException(mue);
//            }
//        }
//        return result;
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
        if( ! HttpMethod.POST.equals( request.method() ) )
            return request.method();

        if( request.headers().contains( Request.METHOD_OVERRIDE_HEADER ) )
        {
            return HttpMethod.valueOf( request.headers().get( Request.METHOD_OVERRIDE_HEADER ) );
        }
        if( qs.containsKey( Request.METHOD_OVERRIDE_PARAMETER ) )
        {
            String method = qs.get( Request.METHOD_OVERRIDE_PARAMETER );
            request.headers().add( Request.METHOD_OVERRIDE_HEADER, method );
            return HttpMethod.valueOf( method );
        }
        return request.method();
    }

    private Map<String, String> parseQueryStringParams()
    {
        Map<String,String> params = new HashMap<>();
        int q = uri.indexOf( "?" );
        String qs = ( q >= 0 ? uri.substring( q+1 ) : null );
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
