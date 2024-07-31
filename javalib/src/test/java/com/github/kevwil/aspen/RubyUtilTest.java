package com.github.kevwil.aspen;

import io.netty.buffer.ByteBuf;
import org.jruby.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Jan 19, 2011
 */
public class RubyUtilTest
{
    private static final Ruby RUNTIME = Ruby.getGlobalRuntime();

    @Test
    public void shouldConvertGoodNumberToInt()
    {
        RubyFixnum ri = RUNTIME.newFixnum( 100 );
        int result = RubyUtil.toInt( ri );
        assertEquals( 100, result );
    }

    @Test
    public void shouldCallMethodOnObject()
    {
        IRubyObject value = RubyString.newString(RUNTIME, "aoeu" );
        IRubyObject result = RubyUtil.call( "upcase", value );
        assertEquals( "AOEU", result.toString() );
    }

    @Test
    public void shouldGetValueFromHash()
    {
        RubyHash hash = RubyHash.newHash(RUNTIME);
        hash.put( "foo", "bar" );
        IRubyObject key = RubyString.newString(RUNTIME, "foo" );
        IRubyObject result = RubyUtil.hashGet( hash, key );
        assertEquals( "bar", result.toString() );
    }

    @Test
    public void shouldDeleteFromHash()
    {
        RubyHash hash = RubyHash.newHash(RUNTIME);
        hash.put( "foo", "bar" );
        IRubyObject key = RubyString.newString(RUNTIME, "foo" );
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
        assertTrue( result.respondsTo( "each_line" ) );
        assertTrue( result.respondsTo( "rewind" ) );
        assertTrue( result.respondsTo( "close" ) );
    }

    @Test
    public void shouldEnumerateRubyIntoBuffer()
    {
        String data = "foo\nbar";
        IRubyObject rubyData = RubyString.newString(RUNTIME, data );
        ByteBuf buffer = RubyUtil.bodyToBuffer( rubyData );
        assertNotNull( buffer );
        String bufferData = buffer.toString( StandardCharsets.UTF_8 );
        assertEquals( data, bufferData );
    }

    @Test
    public void shouldTrimEmptyHeaders()
    {
        RubyHash env = RubyHash.newHash(RUNTIME);
        env.op_aset( RUNTIME.getCurrentContext(), RubyString.newString(RUNTIME, "FOO" ), RUNTIME.getNil() );
        env.op_aset( RUNTIME.getCurrentContext(), RubyString.newString(RUNTIME, "BAR" ), RubyString.newString(RUNTIME, "" ) );
        env.op_aset( RUNTIME.getCurrentContext(), RubyString.newString(RUNTIME, "AOEU" ), RubyString.newString(RUNTIME, "dvorak" ) );
        RubyUtil.trimEmptyValues( env );

        assertFalse( env.containsKey( "FOO" ) );
        assertFalse( env.containsKey( "BAR" ) );
        assertTrue( env.containsKey( "AOEU" ) );
    }

}
