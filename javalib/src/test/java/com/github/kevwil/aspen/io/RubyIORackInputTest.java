package com.github.kevwil.aspen.io;

import org.jboss.netty.buffer.ChannelBuffers;
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
    private static final Ruby _runtime = Ruby.getGlobalRuntime();
    private RubyIORackInput _input;

    @Before
    public void setUp()
    {
        _input = new RubyIORackInput( _runtime );
    }

    @Test( expected = RaiseException.class )
    public void shouldThrowErrorOnClose()
    {
        _input.close();
    }

    @Test
    public void shouldReturnTrueForBinmode()
    {
        assertTrue( _input.getBinmode().isTrue() );
    }

    @Test
    public void shouldReflectEofWhenAllDataIsRead()
    {
        String data = "hello";
        int dataLen = data.length();
        _input.setBuffer( ChannelBuffers.copiedBuffer( data.getBytes() ) );
        IRubyObject dataLenRuby = JavaEmbedUtils.javaToRuby( _runtime, dataLen );
        IRubyObject result = _input.read( _runtime.getCurrentContext(), new IRubyObject[]{dataLenRuby} );
        assertNotNull( result );
        assertTrue( result instanceof RubyString );
        assertEquals( data, result.toString() );
        assertTrue( _input.isEof().isTrue() );
    }

    @Test( expected = RaiseException.class )
    public void shouldRaiseEOFErrorWhenReadingBeyondDataLength()
    {
        String data = "hello";
        int dataLen = 10; // read too much
        _input.setBuffer( ChannelBuffers.copiedBuffer( data.getBytes() ) );
        IRubyObject dataLenRuby = JavaEmbedUtils.javaToRuby( _runtime, dataLen );
        _input.read( _runtime.getCurrentContext(), new IRubyObject[]{dataLenRuby} );
    }

    @Test
    public void shouldRewindAndReadMultipleTimes()
    {
        String data = "hello";
        _input.setBuffer( ChannelBuffers.copiedBuffer( data.getBytes() ) );
        IRubyObject result1 = _input.read( _runtime.getCurrentContext(), new IRubyObject[]{} );
        assertNotNull( result1 );
        assertTrue( result1 instanceof RubyString );
        assertEquals( data, result1.toString() );

        IRubyObject out = _input.rewind( _runtime.getCurrentContext() );
        assertEquals( _runtime.getNil(), out );

        IRubyObject result2 = _input.read( _runtime.getCurrentContext(), new IRubyObject[]{} );
        assertNotNull( result2 );
        assertTrue( result2 instanceof RubyString );
        assertEquals( data, result2.toString() );
    }

    @Test
    public void shouldReadIntoStringBuffer()
    {
        String data = "hello";
        int dataLen = data.length();
        IRubyObject dataLenRuby = JavaEmbedUtils.javaToRuby( _runtime, dataLen );
        _input.setBuffer( ChannelBuffers.copiedBuffer( data.getBytes() ) );

        RubyString buf = RubyString.newEmptyString( _runtime );
        IRubyObject[] args = new IRubyObject[]{dataLenRuby, buf};
        IRubyObject result = _input.read( _runtime.getCurrentContext(), args );
        assertEquals( _runtime.getNil(), result );
        assertEquals( data, buf.toString() );
    }

    @Test
    public void shouldReadFirstLineWhenCallingGets()
    {
        String data = "hello\r\nworld";
        _input.setBuffer( ChannelBuffers.copiedBuffer( data.getBytes() ) );

        IRubyObject result = _input.gets( _runtime.getCurrentContext() );

        assertNotNull( result );
        assertEquals( data.substring( 0, 5 ), result.toString() );
    }

    @Test
    public void shouldYieldEachLineInBuffer()
    {
        final AtomicInteger yieldCount = new AtomicInteger();
        final String data = "line1\r\nline2\r\nline3";
        _input.setBuffer( ChannelBuffers.copiedBuffer( data.getBytes() ) );

        BlockCallback callback = new BlockCallback(){
            public IRubyObject call( ThreadContext context, IRubyObject[] args, Block block ){
                assertTrue( data.contains( args[0].toString() ) );
                yieldCount.getAndIncrement();
                return _runtime.getNil();
            }
        };
        Block block = CallBlock.newCallClosure(
                _input,
                _runtime.getOrCreateModule( "Aspen" ),
                Arity.createArity( 1 ),
                callback,
                _runtime.getCurrentContext() );
        _input.each( _runtime.getCurrentContext(), block );
        assertEquals( 3, yieldCount.get() );
    }
}
