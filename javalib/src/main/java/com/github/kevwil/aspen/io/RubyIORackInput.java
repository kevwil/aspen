package com.github.kevwil.aspen.io;

import com.github.kevwil.aspen.RackInput;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import org.jruby.*;
import org.jruby.anno.JRubyClass;
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
@JRubyClass(name = "AspenRackInput")
public class RubyIORackInput
extends RubyObject
implements RackInput
{
    public static RubyClass createRackInputClass(Ruby runtime)
    {
        RubyClass myClass = runtime.defineClass("AspenRackInput", runtime.getObject(), RubyIORackInput::new);
        myClass.setReifiedClass(RubyIORackInput.class);
        myClass.defineAnnotatedMethods(RubyIORackInput.class);
        return myClass;
    }

    public RubyIORackInput(Ruby runtime)
    {
        super(runtime, RubyIORackInput.createRackInputClass(runtime));
    }

    public RubyIORackInput( Ruby runtime, RubyClass metaClass )
    {
        super( runtime, metaClass );
    }

    /* CLASS DATA */

    private ByteBuf buffer;

    public ByteBuf getBuffer()
    {
        return buffer;
    }

    public void setBuffer( final ByteBuf buffer )
    {
        this.buffer = buffer;
    }

    /* JRuby Methods */

    @JRubyMethod()
    public IRubyObject gets( final ThreadContext context )
    {
        try
        {
            String line = readLine( getBuffer() );
            if( line.isEmpty() )
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
        int len;
        ByteBuf chunk;
        switch( args.length )
        {
            case 0:
                return JavaEmbedUtils.javaToRuby( getRuntime(), bufferToString( getBuffer() ) );
            case 1:
                len = RubyInteger.num2int( args[0] );
                if( len > getBuffer().readableBytes() )
                {
                    throw getRuntime().newIOError( "cannot read " + len + " bytes from input" );
                }
                chunk = getBuffer().readBytes( len );
                return JavaEmbedUtils.javaToRuby( getRuntime(), bufferToString( chunk ) );
            case 2:
                len = RubyInteger.num2int( args[0] );
                chunk = getBuffer().readBytes( len );
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
        try (ByteBufInputStream stream = new ByteBufInputStream(getBuffer().slice())) {
            AtomicReference<String> line = new AtomicReference<>();
            if (!isEof().isTrue()) {
                try {
                    do {
                        line.set(stream.readLine());
                        block.yield(context, JavaEmbedUtils.javaToRuby(getRuntime(), line.get()));
                    }
                    while (line.get() != null && stream.available() > 0);
                } catch (IOException e) {
                    throw getRuntime().newIOError(e.getLocalizedMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return getRuntime().getNil();
    }

    @JRubyMethod()
    public IRubyObject rewind( final ThreadContext context )
    {
        getBuffer().readerIndex( 0 );
        return getRuntime().getNil();
    }

    @JRubyMethod( name = "eof?" )
    public IRubyObject isEof()
    {
        return getRuntime().newBoolean( ! getBuffer().isReadable() );
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


    private String readLine( final ByteBuf buf )
    throws IOException
    {
        int startIndex = buf.readerIndex();
        String line;
        try (ByteBufInputStream stream = new ByteBufInputStream(buf.slice())) {
            line = stream.readLine();
        }
        int newIndex = startIndex + line.length();
        buf.readerIndex( newIndex );
        return line;
    }

    private String bufferToString( ByteBuf buffer )
    {
        byte[] dest = new byte[buffer.readableBytes()];
        buffer.readBytes( dest );
        return new String( dest );
    }
}
