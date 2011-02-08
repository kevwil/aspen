package com.github.kevwil.aspen.io;

import com.github.kevwil.aspen.RackInput;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jruby.*;
import org.jruby.anno.JRubyMethod;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author kevwil
 * @since Jan 27, 2011
 */
public class RubyIORackInput
extends RubyObject
implements RackInput
{
    private static final ObjectAllocator ALLOCATOR = new ObjectAllocator()
    {
        public IRubyObject allocate( Ruby runtime, RubyClass klass )
        {
            return new RubyIORackInput( runtime, klass );
        }
    };

    public static RubyClass getClass(
            Ruby runtime,
            String name,
            RubyClass parent,
            ObjectAllocator allocator,
            Class annoClass)
    {
        RubyModule aspenMod = runtime.getOrCreateModule( "Aspen" );
        RubyClass klass = aspenMod.getClass( name );
        if( klass == null )
        {
            klass = aspenMod.defineClassUnder( name, parent, allocator );
            klass.defineAnnotatedMethods( annoClass );
        }
        return klass;
    }

    public static RubyClass getRubyIORackInputClass( Ruby runtime )
    {
        return getClass( runtime, "RubyIORackInput", runtime.getObject(),
                ALLOCATOR, RubyIORackInput.class );
    }

    public RubyIORackInput( Ruby runtime, RubyClass metaClass )
    {
        super( runtime, metaClass );
    }

    public RubyIORackInput( Ruby runtime )
    {
        super( runtime, getRubyIORackInputClass( runtime ) );
    }

    /* CLASS DATA */

    private ChannelBuffer _buffer;

    public ChannelBuffer getBuffer()
    {
        return _buffer;
    }

    public void setBuffer( final ChannelBuffer buffer )
    {
        _buffer = buffer;
    }

    /* JRuby Methods */

    @JRubyMethod()
    public IRubyObject gets( final ThreadContext context )
    {
        try
        {
            String line = readLine( getBuffer() );
            if( line == null )
            {
                throw getRuntime().newEOFError();
            }
            return JavaEmbedUtils.javaToRuby( getRuntime(), line );
        }
        catch( IOException e )
        {
            throw getRuntime().newIOError( e.getLocalizedMessage() );
        }
    }

    @JRubyMethod( optional = 2 )
    public IRubyObject read( final ThreadContext context, final IRubyObject[] args )
    {
        switch( args.length )
        {
            case 0:
                return JavaEmbedUtils.javaToRuby( getRuntime(), bufferToString( _buffer ) );
            case 1:
                int len = RubyInteger.num2int( args[0] );
                if( len > _buffer.readableBytes() )
                {
                    throw getRuntime().newIOError( "cannot read " + len + " bytes from input" );
                }
                ChannelBuffer chunk = _buffer.readBytes( len );
                return JavaEmbedUtils.javaToRuby( getRuntime(), bufferToString( chunk ) );
            case 2:
                len = RubyInteger.num2int( args[0] );
                chunk = _buffer.readBytes( len );
                RubyString buf = RubyString.stringValue( args[1] );
                buf.append( JavaEmbedUtils.javaToRuby( getRuntime(), bufferToString( chunk ) ) );
                return getRuntime().getNil();
            default:
                throw getRuntime().newArgumentError( "wrong number of arguments: " + args.length );
        }
    }

    @JRubyMethod()
    public IRubyObject each( final ThreadContext context, final Block block )
    {
        ChannelBufferInputStream stream = new ChannelBufferInputStream( _buffer.slice() );
        AtomicReference<String> line = new AtomicReference<String>();
        if( !isEof().isTrue() )
        {
            try
            {
                do
                {
                    line.set( stream.readLine() );
                    block.yield( context, JavaEmbedUtils.javaToRuby( getRuntime(), line.get() ) );
                }
                while( line.get() != null && stream.available() > 0 );
            }
            catch( IOException e )
            {
                throw getRuntime().newIOError( e.getLocalizedMessage() );
            }
        }
        return getRuntime().getNil();
    }

    @JRubyMethod()
    public IRubyObject rewind( final ThreadContext context )
    {
        _buffer.readerIndex( 0 );
        return getRuntime().getNil();
    }

    @JRubyMethod( name = "eof?" )
    public IRubyObject isEof()
    {
        return getRuntime().newBoolean( !_buffer.readable() );
    }

    @JRubyMethod( name = "binmode" )
    public IRubyObject getBinmode()
    {
        return getRuntime().newBoolean( true );
    }

    @JRubyMethod()
    public void close()
    {
        throw getRuntime().newIOError( "Rack spec prohibits calling close() on rack.input stream." );
    }


    private String readLine( final ChannelBuffer buf )
    throws IOException
    {
        int startIndex = buf.readerIndex();
        ChannelBufferInputStream stream = new ChannelBufferInputStream( buf.slice() );
        String line = stream.readLine();
        int newIndex = startIndex + line.length();
        buf.readerIndex( newIndex );
        return line;
    }

    private String bufferToString( ChannelBuffer buffer )
    {
        byte[] dest = new byte[buffer.readableBytes()];
        buffer.readBytes( dest );
        return new String( dest );
    }
}
