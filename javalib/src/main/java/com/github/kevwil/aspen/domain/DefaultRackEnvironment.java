package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.*;
import com.github.kevwil.aspen.exception.ServiceException;
import com.github.kevwil.aspen.input.RackRewindableInput;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jruby.*;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.util.io.STDIO;

import java.io.*;
import java.net.*;

/**
 * @author kevwil
 * @since Jan 28, 2011
 */
public class DefaultRackEnvironment
implements RackEnvironment
{
    private final Ruby _runtime = Ruby.getGlobalRuntime();
    private Request _request;
    private RackInput _input;
    private InputStream _stream;

    public DefaultRackEnvironment( Request request )
    {
        _request = request;
        _stream = new ChannelBufferInputStream( _request.getBody() );
        try
        {
            setRackInput( new RackRewindableInput( _runtime, this ) );
        }
        catch( IOException e )
        {
            throw new ServiceException( e );
        }
    }

    private RubyHash createRubyHash( final Request request )
    {
        RubyHash env = request.getRubyHeaders();
        assignConnectionRelatedCgiHeaders( env, request );
        tweakCgiVariables( env, request.getUri() );
        updateEnv( env, request );
        return env;
    }

    void updateEnv( final RubyHash env, final Request request )
    {
        env.put( "rack.version", Version.RACK );
        env.put( "rack.input", JavaEmbedUtils.javaToRuby( _runtime, getRackInput() ) );
        env.put( "rack.errors", new RubyIO( _runtime, STDIO.ERR ) );
        env.put( "rack.multithread", true );
        env.put( "rack.multiprocess", false );
        env.put( "rack.run_once", false );
        env.put( "rack.url_scheme", request.getUrl().getProtocol() );
    }

    @Override
    public InputStream getInput()
    {
        return _stream;
    }

    @Override
    public int getContentLength()
    {
        return _request.getBodyString().length();
    }

    @Override
    public RackInput getRackInput()
    {
        return _input;
    }

    @Override
    public void setRackInput( final RackInput input )
    {
        _input = input;
    }

    @Override
    public RubyHash toRuby()
    {
        return createRubyHash( _request );
    }

    void assignConnectionRelatedCgiHeaders( final RubyHash env, final Request request )
    {
        String remote = request.getRemoteAddress().toString().replace( "/", "" );
        env.put( "REMOTE_ADDR", remote );
        env.put( "REMOTE_HOST", remote );
        if( !env.containsKey( "SERVER_NAME" ) && !env.containsKey( "SERVER_PORT" ) )
        {
            if( request.containsHeader( HttpHeaders.Names.HOST ) )
            {
                String[] parts = request.getHeader( HttpHeaders.Names.HOST ).split( ":" );
                if( parts.length > 0 )
                {
                    env.put( "SERVER_NAME", parts[0] );
                    if( parts.length > 1 )
                    {
                        env.put( "SERVER_PORT", parts[1] );
                    }
                }
            }
            else
            {
                InetSocketAddress localAddress = (InetSocketAddress) request.getLocalAddress();
                env.put( "SERVER_NAME", localAddress.getHostName() );
                env.put( "SERVER_PORT", String.valueOf( localAddress.getPort() ) );
            }
        }
        env.put( "SERVER_PROTOCOL", request.getHttpRequest().getProtocolVersion().toString() );
    }

    void tweakCgiVariables( final RubyHash env, final String path )
    {
        // Rack-specified rules
        if( env.get( "SCRIPT_NAME" ) == null )
            env.put( "SCRIPT_NAME", "" );
        if( env.get( "SCRIPT_NAME" ).equals( "/" ) )
            env.put( "SCRIPT_NAME", "" );
        if( env.get( "PATH_INFO" ) != null && env.get( "PATH_INFO" ).equals( "" ) )
        {
            env.remove( "PATH_INFO" );
        }
        else
        {
            int snLen = env.get( "SCRIPT_NAME" ).toString().length();
            env.put( "PATH_INFO", path.substring( snLen, path.length() ) );
        }
        if( !env.containsKey( "REQUEST_PATH" ) ||
                env.get( "REQUEST_PATH" ) == null ||
                env.get( "REQUEST_PATH" ).toString().length() == 0 )
        {
            env.put( "REQUEST_PATH", env.get( "SCRIPT_NAME" ).toString() + env.get( "PATH_INFO" ) );
        }
        if( !env.containsKey( "SERVER_PORT" ) )
            env.put( "SERVER_PORT", "80" );

        // CGI-specific headers
        env.put( "PATH_TRANSLATED", env.get( "PATH_INFO" ) );
//        env.put( "QUERY_STRING", path.substring( path.indexOf( "?" ) + 1 ) );
        env.put( "SERVER_SOFTWARE", "Aspen " + Version.ASPEN );
    }

}
