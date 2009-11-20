package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jruby.*;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.ByteList;

import java.io.*;

/**
 * Inspired by classes in JRuby-Rack by nicksieger
 * @author kevwil
 * @since Nov 20, 2009
 */
public class BufferedRackInput implements RackInput
{
    private Ruby _runtime;
    private ChannelBuffer _buffer;
    
    public BufferedRackInput( Ruby runtime, ChannelBuffer content )
    {
        _runtime = runtime;
        _buffer = content;
    }

    private Ruby getRuntime()
    {
        return _runtime;
    }

    /**
     * Only accepting no args - default line separator used.
     * The stream must be opened for reading or an IOError will be raised.
     * The line read in will be returned and also assigned to $_.
     * Returns nil if called at end of file.
     * @param context ruby context
     * @return content line, or nil
     */
    public IRubyObject gets( final ThreadContext context )
    {
        try
        {
            final int NEWLINE = 10;
            byte[] bytes = readUntil( NEWLINE, 0 );
            if( bytes != null )
            {
                RubyString result = getRuntime().newString( new ByteList( bytes ) );
                getRuntime().getGlobalVariables().set( "$_", result );
                return result;
            }
            else
            {
                return getRuntime().getNil();
            }
        }
        catch( IOException io )
        {
            throw getRuntime().newIOErrorFromException( io );
        }
    }

    public IRubyObject read( final ThreadContext context, final IRubyObject[] args )
    {
        long count = 0;
        if( args.length > 0 )
        {
            count = args[0].convertToInteger( "to_i" ).getLongValue();
        }
        RubyString string = null;
        if( args.length == 2 )
        {
            string = args[1].convertToString();
        }

        try
        {
            byte[] bytes = readUntil( Integer.MAX_VALUE, count );
            if( bytes != null )
            {
                if( string != null )
                {
                    string.cat( bytes );
                    return string;
                }
                return getRuntime().newString( new ByteList( bytes ) );
            }
            else
            {
                return RubyString.newEmptyString( getRuntime() );
            }
        }
        catch( IOException io )
        {
            throw getRuntime().newIOErrorFromException( io );
        }
    }

    public IRubyObject each( final ThreadContext context, final Block block )
    {
        IRubyObject nil = getRuntime().getNil();
        IRubyObject line;
        while( ( line = gets( context ) ) != nil )
        {
            block.yield( context, line );
        }
        return nil;
    }

    public IRubyObject rewind( final ThreadContext context )
    {
        _buffer.readerIndex( 0 ); //?????
        return getRuntime().getNil();
    }

    public void close()
    {
    }

    private byte[] readUntil( int match, long count ) throws IOException
    {
        ByteArrayOutputStream bs = null;
        byte b;
        long i = 0;
        do
        {
            if( _buffer.readableBytes() > 0 )
            {
                b = _buffer.readByte();
            }
            else
            {
                break;
            }
            if( bs == null )
            {
                bs = new ByteArrayOutputStream();
            }
            bs.write( b );
            if( count > 0 && ++i == count )
            {
                break;
            }
        }
        while( b != match );

        if( bs == null )
        {
            return null;
        }
        return bs.toByteArray();
    }
}
