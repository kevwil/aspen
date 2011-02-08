package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.*;
import com.github.kevwil.aspen.io.RubyIORackErrors;
import com.github.kevwil.aspen.io.RubyIORackInput;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jruby.*;

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
        RubyIORackInput input = new RubyIORackInput( _runtime );
        input.setBuffer( _request.getBody() );
        setRackInput( input );
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
        env.put( "rack.input", getRackInput() );
        env.put( "rack.errors", new RubyIORackErrors( _runtime ) );
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
        InetSocketAddress remoteAddress = (InetSocketAddress) request.getRemoteAddress();
        String remote = remoteAddress.getHostName().replace( "/", "" );
        env.put( "REMOTE_ADDR", remote );
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
        env.put( "HTTP_VERSION", request.getHttpRequest().getProtocolVersion().toString() );
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
        env.put( "REQUEST_URI", path );
        env.put( "GATEWAY_INTERFACE", "CGI/1.2" );
        env.put( "SERVER_SOFTWARE", "Aspen " + Version.ASPEN );
    }

}
