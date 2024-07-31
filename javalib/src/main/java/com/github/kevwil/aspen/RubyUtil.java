package com.github.kevwil.aspen;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.jruby.*;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.nio.charset.StandardCharsets;

/**
 * @author kevwil
 * @since Jan 19, 2011
 */
public class RubyUtil
{
    private static final Ruby RUNTIME = Ruby.getGlobalRuntime();

    private RubyUtil(){}

    public static int toInt( final IRubyObject obj )
    {
        return obj.convertToInteger().toJava( Integer.class );
    }

    public static IRubyObject call( final String method, final IRubyObject target )
    {
        return target.callMethod( target.getRuntime().getCurrentContext(), method );
    }

    public static IRubyObject hashGet( final RubyHash hash, final IRubyObject key )
    {
        return hash.op_aref( hash.getRuntime().getCurrentContext(), key );
    }

    public static void hashDelete( final RubyHash hash, final IRubyObject key )
    {
        hash.delete( hash.getRuntime().getCurrentContext(), key, Block.NULL_BLOCK );
    }

    public static RubyIO stringToIO( final String input )
    {
        InputStream dataStream = new ByteArrayInputStream(
                input.getBytes( StandardCharsets.UTF_8 ) );
        return RubyIO.newIO(RUNTIME, Channels.newChannel( dataStream ) );
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

    public static ByteBuf bodyToBuffer(final IRubyObject body )
    {
        return Unpooled.copiedBuffer(body.toString(), StandardCharsets.UTF_8);
    }
}
