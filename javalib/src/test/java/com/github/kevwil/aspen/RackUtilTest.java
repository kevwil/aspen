package com.github.kevwil.aspen;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.net.InetSocketAddress;

/**
 * @author kevinw
 * @since Nov 29, 2009
 */
public class RackUtilTest
{
    private String server;
    private String port;
    private ChannelHandlerContext ctx;
    private HttpRequest r;
    private static Ruby ruby;
    private RubyHash env;

    @BeforeClass
    public static void setUpClass()
    {
        ruby = Ruby.getGlobalRuntime();
    }
    
    @Before
    public void setUp()
    {
        server = "localhost";
        port = "8080";
        ctx = RackUtil.buildChannelHandlerContext( server, port );
        r = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://"+server+":"+port+"/" );
        env = RubyHash.newHash( ruby );
    }
    @Test
    public void shouldCreateDummyChannelHandlerContextWithLocalServerAddress() throws Exception
    {
        assertNotNull( ctx );
        assertNotNull( ctx.getChannel() );
        assertNotNull( ctx.getChannel().getLocalAddress() );
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.getChannel().getLocalAddress();
        assertEquals( server, socketAddress.getHostName() );
        assertEquals( port, Integer.toString( socketAddress.getPort() ) );
    }

    @Test
    public void shouldUsePort80() throws Exception
    {
        ctx = RackUtil.buildChannelHandlerContext( server, null );
        assertNotNull( ctx );
        assertNotNull( ctx.getChannel() );
        assertNotNull( ctx.getChannel().getLocalAddress() );
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.getChannel().getLocalAddress();
        assertEquals( server, socketAddress.getHostName() );
        assertEquals( 80, socketAddress.getPort() );
    }

    @Test
    public void shouldParseUriFromContext() throws Exception
    {
        RubyHash env = RubyHash.newHash( ruby );

        RackUtil.doUriRelated( ctx, r, env );

        assertEquals( "", env.get( "QUERY_STRING" ) );
        assertEquals( "/", env.get( "PATH_INFO" ) );
        assertEquals( server, env.get( "SERVER_NAME" ) );
        assertEquals( port, env.get( "SERVER_PORT" ) );
    }

    @Test
    public void shouldParseUriFromHostHeader() throws Exception
    {
        r.addHeader( HttpHeaders.Names.HOST, server+":"+port );

        RackUtil.doUriRelated( null, r, env );

        assertEquals( "", env.get( "QUERY_STRING" ) );
        assertEquals( "/", env.get( "PATH_INFO" ) );
        assertEquals( server, env.get( "SERVER_NAME" ) );
        assertEquals( port, env.get( "SERVER_PORT" ) );
    }
}
