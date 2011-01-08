package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import com.github.kevwil.aspen.exception.InvalidAppException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jruby.*;
import org.jruby.javasupport.JavaClass;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;
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
    private IRubyObject _app;

    public JRubyRackProxy( IRubyObject app )
    {
        _app = app;
    }

    @Override
    public Response process( final Request request )
    {
        if( ! _app.respondsTo( "call" ) )
        {
            throw new InvalidAppException();
        }
        RubyHash env = RubyHash.newHash( RUBY );
        HttpRequest hr = request.getHttpRequest();
        ChannelHandlerContext ctx = request.getContext();
        RackUtil.parseHeaders( ctx, hr, env );
        tweakCgiVariables( env );

        RubyIO input = buildInputStream( request );
        RubyIO errors = new RubyIO( RUBY, STDIO.ERR );
        updateEnv( env, input, errors, request );

        IRubyObject[] args = { env };
        RubyArray result = (RubyArray)_app.callMethod( RUBY.getCurrentContext(),
                                                       "call",
                                                       args,
                                                       Block.NULL_BLOCK );

        return createResponse( request, result );
    }

    Response createResponse( final Request request, final RubyArray result )
    {
        Response r = new Response( request );
        r.setResponseCode( Integer.parseInt( result.get( 0 ).toString() ) );
        RubyHash headers = result.entry( 1 ).convertToHash();
        for( IRubyObject key : headers.keys().toJavaArray() )
        {
            IRubyObject value = headers.op_aref( RUBY.getCurrentContext(), key );
            r.addHeader( key.toString(), value.toString() );
            if( JavaClass.assignable( value.getClass(), headers.getClass() ) )
            {
                RubyHash valueHash = (RubyHash)value;
                for( IRubyObject key1 : valueHash.keys().toJavaArray() )
                {
                    r.addHeader( key.toString(), valueHash.op_aref( RUBY.getCurrentContext(), key1 ).toString() );
                }
            }
        }
        IRubyObject body = result.entry( 2 );

        writeBodyToResponse( body, r );
        return r;
    }

    void writeBodyToResponse( final IRubyObject body, final Response response )
    {
        // TODO: don't use assert to test this
        assert body.respondsTo( "each" );
        final ChannelBuffer outBuffer = ChannelBuffers.dynamicBuffer();
        BlockCallback callback = new BlockCallback(){
            public IRubyObject call( ThreadContext context, IRubyObject[] args, Block block ){
                ChannelBuffer line = ChannelBuffers.copiedBuffer( args[0].toString(), Charset.forName( "UTF-8" ) );
                outBuffer.writeBytes( line );
                return RUBY.getNil();
            }
        };
        RubyEnumerable.callEach( RUBY, RUBY.getCurrentContext(), body, callback );
        response.setBody( outBuffer.toString( Charset.forName( "UTF-8" ) ) );
    }

    void updateEnv( final RubyHash env, final RubyIO input, final RubyIO errors, final Request request )
    {
        env.put( "rack.version", "foo" );
        env.put( "rack.input", input );
        env.put( "rack.errors", errors );
        env.put( "rack.multithread", true );
        env.put( "rack.multiprocess", false );
        env.put( "rack.run_once", false );
        env.put( "rack.url_scheme", request.getUrl().getProtocol() );
    }

    RubyIO buildInputStream( final Request request )
    {
        String data = request.getBodyString();
        InputStream dataStream = new ByteArrayInputStream(
                data.getBytes( Charset.forName( "UTF-8" ) ) );
        RubyIO input = RubyIO.newIO( RUBY, Channels.newChannel( dataStream ) );
        input.binmode();
        return input;
    }

    void tweakCgiVariables( final RubyHash env )
    {
        if( env.get( "SCRIPT_NAME" ).equals( "/" ) )
            env.put( "SCRIPT_NAME", "" );
        if( env.get( "PATH_INFO" ).equals( "" ) )
            env.remove( "PATH_INFO" );
        if( !env.containsKey( "SERVER_PORT" ) )
            env.put( "SERVER_PORT", "80" );
    }
}
