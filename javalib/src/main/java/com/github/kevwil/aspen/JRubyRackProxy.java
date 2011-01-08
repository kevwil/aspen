package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import com.github.kevwil.aspen.rack.RackMiddleware;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jruby.*;
import org.jruby.util.io.STDIO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class JRubyRackProxy
implements RackProxy
{
    private static final Ruby RUBY = Ruby.getGlobalRuntime();
    private RackMiddleware _app;

    public JRubyRackProxy( RackMiddleware app )
    {
        _app = app;
    }

    @Override
    public Response process( final Request request )
    {
        RubyHash env = RubyHash.newHash( RUBY );
        HttpRequest hr = request.getHttpRequest();
        ChannelHandlerContext ctx = request.getContext();
        RackUtil.parseHeaders( ctx, hr, env );
        if( env.get( "SCRIPT_NAME" ).equals( "/" ) )
            env.put( "SCRIPT_NAME", "" );
        if( env.get( "PATH_INFO" ).equals( "" ) )
            env.remove( "PATH_INFO" );
        if( !env.containsKey( "SERVER_PORT" ) )
            env.put( "SERVER_PORT", "80" );

        String data = request.getContext().toString();
        InputStream dataStream = new ByteArrayInputStream(
                data.getBytes( Charset.forName( "UTF-8" ) ) );
        RubyIO input = RubyIO.newIO( RUBY, Channels.newChannel( dataStream ) );
        input.set_encoding( RUBY.getCurrentContext(), Encoding::BINARY );
        RubyIO errors = STDIO.ERR;
    }
}
