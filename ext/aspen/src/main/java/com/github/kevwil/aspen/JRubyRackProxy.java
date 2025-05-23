package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.*;
import com.github.kevwil.aspen.exception.InvalidAppException;
import com.github.kevwil.aspen.exception.ServiceException;
import io.netty.buffer.ByteBuf;
import org.jruby.*;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;

import java.nio.charset.StandardCharsets;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class JRubyRackProxy
implements RackProxy
{
    private final IRubyObject app;
    private static final Object LOCK = new Object();

    public JRubyRackProxy( final IRubyObject app )
    {
        this.app = app;
    }

    public Ruby getRuntime()
    {
        synchronized(LOCK)
        {
            return app.getRuntime();
        }
    }
    
    @Override
    public Response process( final Request request )
    {
        synchronized(LOCK)
        {
            if( ! app.respondsTo( "call" ) )
            {
                throw new InvalidAppException();
            }

            RubyHash env = request.getEnv().toRuby();
            IRubyObject[] args = { env };

            IRubyObject callResult = app.callMethod( request.getRuntime().getCurrentContext(),
                                                           "call",
                                                           args,
                                                           Block.NULL_BLOCK );
            if( callResult.isNil() )
            {
                Response err = new Response();
                err.setException( new ServiceException( "'nil' was returned from the app" ) );
                return err;
            }
            if( callResult.getType().toString().equals( "Rack::File" ) )
            {
                System.out.println( callResult.inspect() );
                // TODO: return a file-based response
                Response err = new Response();
                err.setException( new ServiceException( "body is a Rack::File - need to handle it differently" ) );
                return err;
            }
            try
            {
                RubyArray<IRubyObject> result = (RubyArray<IRubyObject>) callResult;
                return createResponse( request, result );
            }
            catch( Exception e )
            {
                Response err = new Response();
                err.setException( e );
                return err;
            }
        }
    }

    Response createResponse( final Request request, final RubyArray<IRubyObject> result )
    {
        Response r = new Response();
        if( result.size() != 3 )
        {
            r.setException( new ServiceException( "bad rack response: " + result.inspect(
                    result.getRuntime().getCurrentContext()
            ).toString() ) );
            return r;
        }
        IRubyObject body = request.getRuntime().getNil();
        try
        {
            IRubyObject result1 = result.entry( 0 );
            if( result.isNil() )
            {
                r.setException( new ServiceException( "bad rack response, null status code: " + result.inspect(
                        result.getRuntime().getCurrentContext()
                ).toString() ) );
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
//        if( !body.respondsTo( "each" ) )
//        {
//            throw new ServiceException( "response body does not respond to :each" );
//        }
        ByteBuf bodyBuffer = RubyUtil.bodyToBuffer( body );
        response.setBody( bodyBuffer.toString(StandardCharsets.UTF_8) );
    }
}
