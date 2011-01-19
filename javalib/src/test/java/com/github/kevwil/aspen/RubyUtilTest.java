package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jruby.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Jan 19, 2011
 */
public class RubyUtilTest
{
    private static final Ruby _runtime = Ruby.getGlobalRuntime();

    @Test
    public void shouldConvertGoodNumberToInt()
    {
        RubyFixnum ri = _runtime.newFixnum( 100 );
        int result = RubyUtil.toInt( ri );
        assertEquals( 100, result );
    }

    @Test
    public void shouldCallMethodOnObject()
    {
        IRubyObject value = RubyString.newString( _runtime, "aoeu" );
        IRubyObject result = RubyUtil.call( "upcase", value );
        assertEquals( "AOEU", result.toString() );
    }

    @Test
    public void shouldGetValueFromHash()
    {
        RubyHash hash = RubyHash.newHash( _runtime );
        hash.put( "foo", "bar" );
        IRubyObject key = RubyString.newString( _runtime, "foo" );
        IRubyObject result = RubyUtil.hashGet( hash, key );
        assertEquals( "bar", result.toString() );
    }

    @Test
    public void shouldDeleteFromHash()
    {
        RubyHash hash = RubyHash.newHash( _runtime );
        hash.put( "foo", "bar" );
        IRubyObject key = RubyString.newString( _runtime, "foo" );
        RubyUtil.hashDelete( hash, key );
        assertFalse( hash.containsKey( "foo" ) );
        assertTrue( hash.isEmpty() );
    }

    @Test
    public void shouldWrapStringInRubyIO()
    {
        String data = "foo\nbar";
        RubyIO result = RubyUtil.stringToIO( data );
        assertFalse( result.isNil() );
        assertTrue( result.respondsTo( "each" ) );
        assertTrue( result.respondsTo( "rewind" ) );
        assertTrue( result.respondsTo( "close" ) );
    }

    @Test
    public void shouldEnumerateRubyIntoBuffer()
    {
        String data = "foo\nbar";
        IRubyObject rubyData = RubyString.newString( _runtime, data );
        ChannelBuffer buffer = RubyUtil.bodyToBuffer( rubyData );
        assertNotNull( buffer );
        String bufferData = buffer.toString( Charset.forName( "UTF-8" ) );
        assertEquals( data, bufferData );
    }

    @Test
    public void shouldTrimEmptyHeaders()
    {
        RubyHash env = RubyHash.newHash( _runtime );
        env.op_aset( _runtime.getCurrentContext(), RubyString.newString( _runtime, "FOO" ), _runtime.getNil() );
        env.op_aset( _runtime.getCurrentContext(), RubyString.newString( _runtime, "BAR" ), RubyString.newString( _runtime, "" ) );
        env.op_aset( _runtime.getCurrentContext(), RubyString.newString( _runtime, "AOEU" ), RubyString.newString( _runtime, "dovorak" ) );
        RubyUtil.trimEmptyValues( env );

        assertFalse( env.containsKey( "FOO" ) );
        assertFalse( env.containsKey( "BAR" ) );
        assertTrue( env.containsKey( "AOEU" ) );
    }

}
