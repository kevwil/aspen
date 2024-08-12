package com.github.kevwil.aspen.io;

import io.netty.buffer.Unpooled;
import org.jruby.*;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.*;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Feb 03, 2011
 */
public class RubyIORackInputTest
{
    private static final Ruby RUNTIME = Ruby.getGlobalRuntime();
    private RubyIORackInput input;

    @Before
    public void setUp()
    {
        input = new RubyIORackInput(RUNTIME, RubyIORackInput.createRackInputClass(RUNTIME) );
    }

    @Test( expected = RaiseException.class )
    public void shouldThrowErrorOnClose()
    {
        input.close();
    }

    @Test
    public void shouldReturnTrueForBinmode()
    {
        assertTrue( input.getBinmode().isTrue() );
    }

    @Test
    public void shouldReflectEofWhenAllDataIsRead()
    {
        String data = "hello";
        int dataLen = data.length();
        input.setBuffer( Unpooled.copiedBuffer( data.getBytes() ) );
        IRubyObject dataLenRuby = JavaEmbedUtils.javaToRuby(RUNTIME, dataLen );
        IRubyObject result = input.read( RUNTIME.getCurrentContext(), new IRubyObject[]{dataLenRuby} );
        assertNotNull( result );
        assertTrue( result instanceof RubyString );
        assertEquals( data, result.toString() );
        assertTrue( input.isEof().isTrue() );
    }

    @Test( expected = RaiseException.class )
    public void shouldRaiseEOFErrorWhenReadingBeyondDataLength()
    {
        String data = "hello";
        int dataLen = 10; // read too much
        input.setBuffer( Unpooled.copiedBuffer( data.getBytes() ) );
        IRubyObject dataLenRuby = JavaEmbedUtils.javaToRuby(RUNTIME, dataLen );
        input.read( RUNTIME.getCurrentContext(), new IRubyObject[]{dataLenRuby} );
    }

    @Test
    public void shouldRewindAndReadMultipleTimes()
    {
        String data = "hello";
        input.setBuffer( Unpooled.copiedBuffer( data.getBytes() ) );
        IRubyObject result1 = input.read( RUNTIME.getCurrentContext(), new IRubyObject[]{} );
        assertNotNull( result1 );
        assertTrue( result1 instanceof RubyString );
        assertEquals( data, result1.toString() );

        IRubyObject out = input.rewind( RUNTIME.getCurrentContext() );
        assertEquals( RUNTIME.getNil(), out );

        IRubyObject result2 = input.read( RUNTIME.getCurrentContext(), new IRubyObject[]{} );
        assertNotNull( result2 );
        assertTrue( result2 instanceof RubyString );
        assertEquals( data, result2.toString() );
    }

    @Test
    public void shouldReadIntoStringBuffer()
    {
        String data = "hello";
        int dataLen = data.length();
        IRubyObject dataLenRuby = JavaEmbedUtils.javaToRuby(RUNTIME, dataLen );
        input.setBuffer( Unpooled.copiedBuffer( data.getBytes() ) );

        RubyString buf = RubyString.newEmptyString(RUNTIME);
        IRubyObject[] args = new IRubyObject[]{dataLenRuby, buf};
        IRubyObject result = input.read( RUNTIME.getCurrentContext(), args );
        assertEquals( RUNTIME.getNil(), result );
        assertEquals( data, buf.toString() );
    }

    @Test
    public void shouldReadFirstLineWhenCallingGets()
    {
        String data = "hello\r\nworld";
        input.setBuffer( Unpooled.copiedBuffer( data.getBytes() ) );

        IRubyObject result = input.gets( RUNTIME.getCurrentContext() );

        assertNotNull( result );
        assertEquals( data.substring( 0, 5 ), result.toString() );
    }

    @Test
    public void shouldYieldEachLineInBuffer()
    {
        final AtomicInteger yieldCount = new AtomicInteger();
        final String data = "line1\r\nline2\r\nline3";
        input.setBuffer( Unpooled.copiedBuffer( data.getBytes() ) );

        BlockCallback callback = new BlockCallback(){
            public IRubyObject call( ThreadContext context, IRubyObject[] args, Block block ){
                assertTrue( data.contains( args[0].toString() ) );
                yieldCount.getAndIncrement();
                return RUNTIME.getNil();
            }
        };
        Block block = CallBlock.newCallClosure(
                RUNTIME.getCurrentContext(),
                input,
                Signature.fromArityValue(1),
                callback
        );
        input.each( RUNTIME.getCurrentContext(), block );
        assertEquals( 3, yieldCount.get() );
    }
}
