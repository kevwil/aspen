package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jruby.*;
import org.jruby.runtime.builtin.IRubyObject;

import java.net.*;
import java.util.*;

/**
 *
 * @author kevwil
 */
public class RackEnvironmentMaker
{
    private static final List<String> allowedSchemes = Arrays.asList("http","https");

    static IRubyObject build(HttpRequest httpRequest, Ruby runtime)
    throws RackException
    {
        RubyHash env = new RubyHash(runtime);
        env.put("rack.version", getRackVersion(runtime));
        URI uri = getUri(httpRequest);
        assignUrlScheme(uri, env);
        env.put("rack.multithread", runtime.getFalse());
        env.put("rack.multiprocess", runtime.getFalse());
        env.put("rack.run_once", runtime.getFalse());
        env.put("REQUEST_METHOD", httpRequest.getMethod().toString());
        env.put("SCRIPT_NAME", "");
        assignPathInfo( uri, env);
        env.put("QUERY_STRING", uri.getQuery());
        env.put("SERVER_NAME", uri.getHost());
        env.put("SERVER_PORT", uri.getPort());
        parseHttpHeaders(httpRequest, env);

        assignInputStream(httpRequest, env);
        assignOutputStream(httpRequest, env);
        /*
         * TODO:
         * NEED TO ASSIGN SESSION FROM APP FIRST,
         * USE DEFAULT IF NOT ASSIGNED
         *
         */
        return env;
    }

    private static void assignPathInfo(URI uri, RubyHash env)
    throws RackException
    {
        String path = uri.getPath();
        if (path.length() > 0 && !path.startsWith("/"))
        {
            throw new RackException("invalid path, must start with '/'");
        }
        else
        {
            env.put("PATH_INFO", path);
        }
    }

    private static void assignUrlScheme(URI uri, RubyHash env)
    throws RackException
    {
        String urlScheme = uri.getScheme();
        if (allowedSchemes.contains(urlScheme))
        {
            env.put("rack.url_scheme", urlScheme);
        }
        else
        {
            throw new RackException("invalid url scheme: " + urlScheme);
        }
    }

    private static RubyArray getRackVersion(Ruby runtime)
    {
        RubyArray version = RubyArray.newArray(runtime);
        version.add(1);
        version.add(0);
        return version;
    }

    private static URI getUri(HttpRequest request)
    {
        try
        {
            return new URI(request.getUri());
        }
        catch( URISyntaxException ex )
        {
            ex.hashCode(); // ignore exception, no IDE warnings
            return null;
        }
    }

    private static void parseHttpHeaders(HttpRequest httpRequest, RubyHash env)
    throws RackException
    {
        for( String key : httpRequest.getHeaderNames() )
        {
            if( key.contains("HTTP_") )
            {
                if( key.equals("HTTP_CONTENT_TYPE") )
                {
                    env.put( "CONTENT_TYPE", httpRequest.getHeader( key ) );
                }
                else if( key.equals("HTTP_CONTENT_LENGTH") )
                {
                    String value = httpRequest.getHeader(key);
                    if( ! value.matches("^\\d+$") )
                    {
                        throw new RackException("CONTENT_LENGTH must consist of digits only.");
                    }
                    else
                    {
                        env.put("CONTENT_LENGTH", value);
                    }
                }
                else
                {
                    env.put( key, httpRequest.getHeader(key) );
                }
            }
        }
    }

    private static void assignInputStream(HttpRequest httpRequest, RubyHash env)
    {
        env.put("rack.input", new ChannelBufferInputStream(
                httpRequest.getContent()));
    }

    private static void assignOutputStream(HttpRequest httpRequest, RubyHash env)
    {
        env.put("rack.errors", env.getRuntime().getStandardError() );
    }
}
