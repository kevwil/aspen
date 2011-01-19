package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import org.jruby.runtime.Block;
import org.jruby.runtime.ThreadContext;
import org.jruby.runtime.builtin.IRubyObject;
import org.jruby.util.io.STDIO;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.charset.Charset;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Jan 07, 2011
 */
public class JRubyRackProxyTest
{
    private static final Ruby _runtime = Ruby.getGlobalRuntime();
    private JRubyRackProxy _rack;
    private IRubyObject _app;
    private ChannelHandlerContext ctx;
    private HttpRequest hr;
    private Request r;

    @Before
    public void startUp()
    {
        _app = createMock( IRubyObject.class );
        _rack = new JRubyRackProxy( _app );
        ctx = RackUtil.buildDummyChannelHandlerContext( "localhost", "80" );
        hr = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        r = new Request( ctx, hr );
    }

    @After
    public void tearDown()
    {
        verify( _app );
    }

    @Test
    public void shouldUpdateCGIVariables()
    {
        replay( _app );
        RubyHash env = RubyHash.newHash( _runtime );
        env.put( "SCRIPT_NAME", "/" );
        env.put( "PATH_INFO", "" );

        _rack.tweakCgiVariables( env, "/" );

        assertEquals( "", env.get( "SCRIPT_NAME" ) );
        assertFalse( env.containsKey( "PATH_INFO" ) );
        assertTrue( env.containsKey( "SERVER_PORT" ) );
        assertEquals( "80", env.get( "SERVER_PORT" ) );
    }

    @Test
    public void shouldUpdateCGIVariablesWithPathInfo()
    {
        replay( _app );
        RubyHash env = RubyHash.newHash( _runtime );
        env.put( "SCRIPT_NAME", "/" );
        env.put( "PATH_INFO", "/aoeu" );

        _rack.tweakCgiVariables( env, "/aoeu" );

        assertEquals( "", env.get( "SCRIPT_NAME" ) );
        assertTrue( env.containsKey( "PATH_INFO" ) );
        assertEquals( "/aoeu", env.get( "PATH_INFO" ) );
        assertTrue( env.containsKey( "SERVER_PORT" ) );
        assertEquals( "80", env.get( "SERVER_PORT" ) );
    }

    @Test
    public void shouldBuildInputStream()
    {
        replay( _app );
        String data = "foo=bar";
        r.setBody( ChannelBuffers.copiedBuffer( data, Charset.forName( "UTF-8" ) ) );

        RubyIO result = _rack.buildInputStream( r );

        assertNotNull( result );

        assertEquals( data, result.read( _runtime.getCurrentContext() ).toString() );
    }

    @Test
    public void shouldUpdateEnv()
    {
        replay( _app );
        RubyHash env = RubyHash.newHash( _runtime );
        RubyIO input = RubyIO.newIO( _runtime, Channels.newChannel(
                new ByteArrayInputStream( "foo".getBytes( Charset.forName( "UTF-8" ) ) ) ) );
        RubyIO errors = new RubyIO( _runtime, STDIO.ERR );

        _rack.updateEnv( env, input, errors, r );

        assertEquals( Version.RACK, env.get( "rack.version" ) );
        assertEquals( input, env.get( "rack.input" ) );
        assertEquals( errors, env.get( "rack.errors" ) );
        assertEquals( true, env.get( "rack.multithread" ) );
        assertEquals( false, env.get( "rack.multiprocess" ) );
        assertEquals( false, env.get( "rack.run_once" ) );
        assertEquals( "http", env.get( "rack.url_scheme" ) );
    }

    @Test
    public void shouldWriteBodyToResponse()
    {
        replay( _app );
        Response response = new Response( r );
        String data = "line one\r\nline two\r\nline three";
        RubyString body = RubyString.newString( _runtime, data );

        _rack.writeBodyToResponse( body, response );

        assertTrue( response.hasBody() );
        assertFalse( response.hasException() );
        assertEquals( data, response.getBody().toString() );
    }

    @Test
    public void shouldCreateResponseFromRackArray()
    {
        replay( _app );
        RubyArray array = RubyArray.newArray( _runtime );
        array.add( 200 );
        RubyHash headers = RubyHash.newHash( _runtime );
        headers.put( "X-Content", "foo" );
        RubyHash cookies = RubyHash.newHash( _runtime );
        cookies.put( "foo_content", "foo" );
        cookies.put( "bar_content", "bar" );
        headers.put( "Cookie", cookies );
        array.add( headers );
        array.add( "Hello World!" );

        Response response = _rack.createResponse( r, array );

        assertNotNull( response );
        assertTrue( response.hasBody() );
        assertFalse( response.hasException() );
        assertEquals( 200, response.getResponseStatus().getCode() );
        assertFalse( response.getHeaderNames().isEmpty() );
        assertEquals( "Hello World!", response.getBody().toString() );
    }

    @Test
    public void shouldCallRackApp()
    {
        RubyArray array = RubyArray.newArray( _runtime );
        array.add( 200 );
        RubyHash headers = RubyHash.newHash( _runtime );
        headers.put( "X-Content", "foo" );
        array.add( headers );
        array.add( "Hello World!" );

        expect( _app.respondsTo( eq( "call" ) ) ).andReturn( true );
        expect( _app.callMethod( anyObject( ThreadContext.class ),
                                 eq( "call" ),
                                 anyObject( IRubyObject[].class ),
                                 eq( Block.NULL_BLOCK ) ) ).andReturn( array );
        replay( _app );

        Response response = _rack.process( r );
        assertNotNull( response );
        assertTrue( response.hasBody() );
        assertFalse( response.hasException() );
        assertEquals( 200, response.getResponseStatus().getCode() );
        assertFalse( response.getHeaderNames().isEmpty() );
        assertEquals( "Hello World!", response.getBody().toString() );
    }
}
