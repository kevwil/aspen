package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.*;
import com.github.kevwil.aspen.exception.InvalidAppException;
import com.github.kevwil.aspen.exception.ServiceException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jruby.*;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.io.STDIO;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class JRubyRackProxy
implements RackProxy
{
    private final Ruby RUBY = Ruby.getGlobalRuntime();
    private final IRubyObject _app;

    public JRubyRackProxy( final IRubyObject app )
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
//        RubyHash env = RubyHash.newHash( RUBY );
//        HttpRequest hr = request.getHttpRequest();
//        ChannelHandlerContext ctx = request.getContext();
//        RackUtil.parseHeaders( ctx, hr, env );
//        assignConnectionRelatedCgiHeaders( env, ctx, hr );
//        tweakCgiVariables( env, hr.getUri() );
//
//        RubyIO input = buildInputStream( request );
//        RubyIO errors = new RubyIO( RUBY, STDIO.ERR );
//        updateEnv( env, input, errors, request );

        RubyHash env = request.getEnv().toRuby();
        IRubyObject[] args = { env };
//        RubyObject.puts( env.inspect() );
        IRubyObject callResult = _app.callMethod( RUBY.getCurrentContext(),
                                                       "call",
                                                       args,
                                                       Block.NULL_BLOCK );
//        RubyObject.puts( callResult.inspect() );
        if( callResult.isNil() )
        {
            Response err = new Response( request );
            err.setException( new ServiceException( "'nil' was returned from the app" ) );
            return err;
        }
        if( callResult.getType().toString().equals( "Rack::File" ) )
        {
            RubyObject.puts( callResult.inspect() );
            // TODO: return a file-based response
            Response err = new Response( request );
            err.setException( new ServiceException( "body is a Rack::File - need to handle it differently" ) );
            return err;
        }
        try
        {
            RubyArray result = (RubyArray)callResult;
            return createResponse( request, result );
        }
        catch( Exception e )
        {
            Response err = new Response( request );
            err.setException( e );
            return err;
        }
    }

//    private void assignConnectionRelatedCgiHeaders( final RubyHash env, final ChannelHandlerContext ctx, final HttpRequest hr )
//    {
//        String remote = ctx.getChannel().getRemoteAddress().toString();
//        env.put( "REMOTE_ADDR", remote );
//        env.put( "REMOTE_HOST", remote );
//        if( !env.containsKey( "SERVER_NAME" ) && !env.containsKey( "SERVER_PORT" ) )
//        {
//            if( hr.containsHeader( HttpHeaders.Names.HOST ) )
//            {
//                String[] parts = hr.getHeader( HttpHeaders.Names.HOST ).split( ":" );
//                if( parts.length > 0 )
//                {
//                    env.put( "SERVER_NAME", parts[0] );
//                    if( parts.length > 1 )
//                    {
//                        env.put( "SERVER_PORT", parts[1] );
//                    }
//                }
//            }
//            else
//            {
//                InetSocketAddress localAddress = (InetSocketAddress) ctx.getChannel().getLocalAddress();
//                env.put( "SERVER_NAME", localAddress.getHostName() );
//                env.put( "SERVER_PORT", String.valueOf( localAddress.getPort() ) );
//            }
//        }
//        env.put( "SERVER_PROTOCOL", hr.getProtocolVersion().toString() );
//    }

    Response createResponse( final Request request, final RubyArray result )
    {
        Response r = new Response( request );
        if( result.size() != 3 )
        {
            r.setException( new ServiceException( "bad rack response: " + result.inspect().toString() ) );
            return r;
        }
        IRubyObject body = RUBY.getNil();
        try
        {
            IRubyObject result1 = result.entry( 0 );
            if( result.isNil() )
            {
                r.setException( new ServiceException( "bad rack response, null status code: " + result.inspect().toString() ) );
                return r;
            }
            int codeInt = RubyInteger.num2int( result1 );
            if( codeInt < 100 )
            {
                r.setException( new ServiceException( "bad rack response, status code integer less than 100" ) );
                return r;
            }
            r.setResponseCode( codeInt );

            RubyHash headers = result.entry( 1 ).convertToHash();
            r.addHeaders( headers );

            body = result.entry( 2 );

            writeBodyToResponse( body, r );
            return r;
        }
        finally
        {
            if( body.respondsTo( "close" ) )
            {
                RubyUtil.call( "close", body );
            }
        }
    }

    void writeBodyToResponse( final IRubyObject body, final Response response )
    {
        if( !body.respondsTo( "each" ) )
        {
            throw new ServiceException( "response body does not respond to :each" );
        }
        ChannelBuffer bodyBuffer = RubyUtil.bodyToBuffer( body );
        response.setBody( bodyBuffer.toString( Charset.forName( "UTF-8" ) ) );
    }

//    void updateEnv( final RubyHash env, final RubyIO input, final RubyIO errors, final Request request )
//    {
//        env.put( "rack.version", Version.RACK );
//        env.put( "rack.input", input );
//        env.put( "rack.errors", errors );
//        env.put( "rack.multithread", true );
//        env.put( "rack.multiprocess", false );
//        env.put( "rack.run_once", false );
//        env.put( "rack.url_scheme", request.getUrl().getProtocol() );
//    }

//    RubyIO buildInputStream( final Request request )
//    {
//        RubyIO input = RubyUtil.stringToIO( request.getBodyString() );
//        input.binmode();
//        return input;
//    }

//    void tweakCgiVariables( final RubyHash env, final String path )
//    {
//        // Rack-specified rules
//        if( env.get( "SCRIPT_NAME" ).equals( "/" ) )
//            env.put( "SCRIPT_NAME", "" );
//        if( env.get( "PATH_INFO" ).equals( "" ) )
//        {
//            env.remove( "PATH_INFO" );
//        }
//        else
//        {
//            int snLen = env.get( "SCRIPT_NAME" ).toString().length();
//            env.put( "PATH_INFO", path.substring( snLen, path.length() ) );
//        }
//        if( !env.containsKey( "REQUEST_PATH" ) ||
//                env.get( "REQUEST_PATH" ) == null ||
//                env.get( "REQUEST_PATH" ).toString().length() == 0 )
//        {
//            env.put( "REQUEST_PATH", env.get( "SCRIPT_NAME" ).toString() + env.get( "PATH_INFO" ) );
//        }
//        if( !env.containsKey( "SERVER_PORT" ) )
//            env.put( "SERVER_PORT", "80" );
//
//        // CGI-specific headers
//        env.put( "PATH_TRANSLATED", env.get( "PATH_INFO" ) );
////        env.put( "QUERY_STRING", path.substring( path.indexOf( "?" ) + 1 ) );
//        env.put( "SERVER_SOFTWARE", "Aspen " + Version.ASPEN );
//    }
}
