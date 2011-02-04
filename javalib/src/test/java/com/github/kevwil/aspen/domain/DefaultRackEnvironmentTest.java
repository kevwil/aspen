package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.*;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.*;

import java.io.*;
import java.nio.charset.Charset;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * @author kevwil
 * @since Jan 31, 2011
 */
public class DefaultRackEnvironmentTest
{
    private final Ruby _runtime = Ruby.getGlobalRuntime();
    private Request r;
    private DefaultRackEnvironment env;

    @Before
    public void startUp()
    {
        ChannelHandlerContext ctx = RackUtil.buildDummyChannelHandlerContext( "localhost", "80" );
        HttpRequest hr = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        r = new Request( ctx, hr );
        env = new DefaultRackEnvironment( r );
    }

    @Test
    public void shouldUpdateCGIVariables()
    {
        RubyHash hash = RubyHash.newHash( _runtime );
        hash.put( "SCRIPT_NAME", "/" );
        hash.put( "PATH_INFO", "" );

        env.tweakCgiVariables( hash, "/" );

        assertEquals( "", hash.get( "SCRIPT_NAME" ) );
        assertFalse( hash.containsKey( "PATH_INFO" ) );
        assertTrue( hash.containsKey( "SERVER_PORT" ) );
        assertEquals( "80", hash.get( "SERVER_PORT" ) );
    }

    @Test
    public void shouldUpdateCGIVariablesWithPathInfo()
    {
        RubyHash hash = RubyHash.newHash( _runtime );
        hash.put( "SCRIPT_NAME", "/" );
        hash.put( "PATH_INFO", "/hello" );

        env.tweakCgiVariables( hash, "/hello" );

        assertEquals( "", hash.get( "SCRIPT_NAME" ) );
        assertTrue( hash.containsKey( "PATH_INFO" ) );
        assertEquals( "/hello", hash.get( "PATH_INFO" ) );
        assertTrue( hash.containsKey( "SERVER_PORT" ) );
        assertEquals( "80", hash.get( "SERVER_PORT" ) );
    }

    @Test
    public void shouldBuildInputStream() throws Exception
    {
        String data = "foo=bar";
        r.setBody( ChannelBuffers.copiedBuffer( data+"\n", Charset.forName( "UTF-8" ) ) );
        env = new DefaultRackEnvironment( r );

        InputStream stream = env.getInput();
        assertNotNull( stream );
        BufferedReader reader = new BufferedReader( new InputStreamReader( stream ) );
        assertEquals( data, reader.readLine() );

        RackInput input = env.getRackInput();
        assertNotNull( input );
        // TODO: find a way to verify the content of the input
    }

    @Test
    public void shouldUpdateEnv()
    {
        RubyHash hash = RubyHash.newHash( _runtime );
        env.updateEnv( hash, r );

        assertEquals( Version.RACK, hash.get( "rack.version" ) );
        assertEquals( JavaEmbedUtils.javaToRuby( _runtime, env.getRackInput() ), hash.get( "rack.input" ) );
        assertTrue( hash.get( "rack.errors" ) instanceof RackErrors );
        assertEquals( true, hash.get( "rack.multithread" ) );
        assertEquals( false, hash.get( "rack.multiprocess" ) );
        assertEquals( false, hash.get( "rack.run_once" ) );
        assertEquals( "http", hash.get( "rack.url_scheme" ) );
    }

}
