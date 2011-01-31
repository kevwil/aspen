package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jruby.*;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;

/**
 * @author kevwil
 * @since Jan 19, 2011
 */
public class RubyUtil
{
    private static final Ruby _runtime = Ruby.getGlobalRuntime();

    private RubyUtil(){}

    public static int toInt( final IRubyObject obj )
    {
        return (Integer) obj.convertToInteger().toJava( Integer.class );
    }

    public static IRubyObject call( final String method, final IRubyObject target )
    {
        return target.callMethod( _runtime.getCurrentContext(), method );
    }

    public static IRubyObject hashGet( final RubyHash hash, final IRubyObject key )
    {
        return hash.op_aref( _runtime.getCurrentContext(), key );
    }

    public static void hashDelete( final RubyHash hash, final IRubyObject key )
    {
        hash.delete( _runtime.getCurrentContext(), key, Block.NULL_BLOCK );
    }

    public static RubyIO stringToIO( final String input )
    {
        InputStream dataStream = new ByteArrayInputStream(
                input.getBytes( Charset.forName( "UTF-8" ) ) );
        return RubyIO.newIO( _runtime, Channels.newChannel( dataStream ) );
    }

    public static void trimEmptyValues( RubyHash env )
    {
        for( IRubyObject key : env.keys().toJavaArray() )
        {
            IRubyObject value = hashGet( env, key );
            if( value.isNil() || value.toString().isEmpty() )
            {
                hashDelete( env, key );
            }
        }
    }

    public static ChannelBuffer bodyToBuffer( final IRubyObject body )
    {
        final ChannelBuffer outBuffer = ChannelBuffers.dynamicBuffer();
        BlockCallback callback = new BlockCallback(){
            public IRubyObject call( ThreadContext context, IRubyObject[] args, Block block ){
                ChannelBuffer line = ChannelBuffers.copiedBuffer( args[0].toString(), Charset.forName( "UTF-8" ) );
                outBuffer.writeBytes( line );
                return _runtime.getNil();
            }
        };
        RubyEnumerable.callEach( _runtime, _runtime.getCurrentContext(), body, callback );
        return outBuffer;
    }
}
