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

    @Test
    public void shouldParseServerPortWhenNoneGiven() throws Exception
    {
        ctx = RackUtil.buildChannelHandlerContext( server, null );
        r = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://"+server+"/" );
        RackUtil.doUriRelated( ctx, r, env );

        assertEquals( "", env.get( "QUERY_STRING" ) );
        assertEquals( "/", env.get( "PATH_INFO" ) );
        assertEquals( server, env.get( "SERVER_NAME" ) );
        Object server_port = env.get("SERVER_PORT");
        assertNotNull(server_port);
        assertEquals( "80", server_port);
    }

    @Test
    public void shouldHandleHeaders() throws Exception
    {
        r.addHeader( HttpHeaders.Names.ACCEPT, "*/*" );
        r.addHeader( HttpHeaders.Names.ACCEPT_CHARSET, "accept-charset" );
        r.addHeader( HttpHeaders.Names.ACCEPT_ENCODING, "accept-encoding" );
        r.addHeader( HttpHeaders.Names.ACCEPT_LANGUAGE, "accept-language" );
        r.addHeader( HttpHeaders.Names.ACCEPT_RANGES, "accept-ranges" );
        r.addHeader( HttpHeaders.Names.AGE, "age" );
        r.addHeader( HttpHeaders.Names.ALLOW, "allow" );
        r.addHeader( HttpHeaders.Names.AUTHORIZATION, "authorization" );
        r.addHeader( HttpHeaders.Names.CACHE_CONTROL, "cache_control" );
        r.addHeader( HttpHeaders.Names.CONNECTION, "connection" );
        r.addHeader( HttpHeaders.Names.CONTENT_ENCODING, "content_encoding" );
        r.addHeader( HttpHeaders.Names.CONTENT_LANGUAGE, "content_language" );
        r.addHeader( HttpHeaders.Names.CONTENT_LENGTH, "12" );
        r.addHeader( HttpHeaders.Names.CONTENT_LOCATION, "content_location" );
        r.addHeader( HttpHeaders.Names.CONTENT_MD5, "content_md5" );
        r.addHeader( HttpHeaders.Names.CONTENT_RANGE, "content_range" );
        r.addHeader( HttpHeaders.Names.CONTENT_TRANSFER_ENCODING, "content_transfer_encoding" );
        r.addHeader( HttpHeaders.Names.CONTENT_TYPE, "content_type" );
        r.addHeader( HttpHeaders.Names.COOKIE, "cookie" );
        r.addHeader( HttpHeaders.Names.DATE, "date" );
        r.addHeader( HttpHeaders.Names.ETAG, "etag" );
        r.addHeader( HttpHeaders.Names.EXPECT, "expect" );
        r.addHeader( HttpHeaders.Names.EXPIRES, "expires" );
        r.addHeader( HttpHeaders.Names.FROM, "from" );
        r.addHeader( HttpHeaders.Names.HOST, "host" );
        r.addHeader( HttpHeaders.Names.IF_MATCH, "if-match" );
        r.addHeader( HttpHeaders.Names.IF_MODIFIED_SINCE, "if-modified-since" );
        r.addHeader( HttpHeaders.Names.IF_NONE_MATCH, "if-none-match" );
        r.addHeader( HttpHeaders.Names.IF_RANGE, "if-range" );
        r.addHeader( HttpHeaders.Names.IF_UNMODIFIED_SINCE, "if-unmodified-since" );
        r.addHeader( HttpHeaders.Names.LAST_MODIFIED, "last-modified" );
        r.addHeader( HttpHeaders.Names.LOCATION, "location" );
        r.addHeader( HttpHeaders.Names.MAX_FORWARDS, "max-forwards" );
        r.addHeader( HttpHeaders.Names.PRAGMA, "pragma" );
        r.addHeader( HttpHeaders.Names.PROXY_AUTHENTICATE, "proxy-authenticate" );
        r.addHeader( HttpHeaders.Names.PROXY_AUTHORIZATION, "proxy-authorization" );
        r.addHeader( HttpHeaders.Names.RANGE, "range" );
        r.addHeader( HttpHeaders.Names.REFERER, "referer" );
        r.addHeader( HttpHeaders.Names.RETRY_AFTER, "retry-after" );
        r.addHeader( HttpHeaders.Names.SERVER, "server" );
        r.addHeader( HttpHeaders.Names.SET_COOKIE, "set-cookie" );
        r.addHeader( HttpHeaders.Names.SET_COOKIE2, "range" );
        r.addHeader( HttpHeaders.Names.TE, "te" );
        r.addHeader( HttpHeaders.Names.TRAILER, "trailer" );
        r.addHeader( HttpHeaders.Names.TRANSFER_ENCODING, "transfer-encoding" );
        r.addHeader( HttpHeaders.Names.UPGRADE, "upgrade" );
        r.addHeader( HttpHeaders.Names.USER_AGENT, "user-agent" );
        r.addHeader( HttpHeaders.Names.VARY, "vary" );
        r.addHeader( HttpHeaders.Names.VIA, "via" );
        r.addHeader( HttpHeaders.Names.WARNING, "warning" );
        r.addHeader( HttpHeaders.Names.WWW_AUTHENTICATE, "www-authenticate" );

        RackUtil.parseHeaders( ctx, r, env );

        assertNotNull( env.get( "HTTP_ACCEPT" ) );
        assertNotNull( env.get( "HTTP_ACCEPT_CHARSET" ) );
        assertNotNull( env.get( "HTTP_ACCEPT_ENCODING" ) );
        assertNotNull( env.get( "HTTP_ACCEPT_LANGUAGE" ) );
        assertNotNull( env.get( "HTTP_ACCEPT_RANGES" ) );
        assertNotNull( env.get( "HTTP_AGE" ) );
        assertNotNull( env.get( "HTTP_ALLOW" ) );
        assertNotNull( env.get( "HTTP_AUTHORIZATION" ) );
        assertNotNull( env.get( "HTTP_CACHE_CONTROL" ) );
        assertNotNull( env.get( "HTTP_CONNECTION" ) );
        assertNotNull( env.get( "HTTP_CONTENT_ENCODING" ) );
        assertNotNull( env.get( "HTTP_CONTENT_LANGUAGE" ) );
        assertNotNull( env.get( "CONTENT_LENGTH" ) );
        assertNotNull( env.get( "HTTP_CONTENT_LOCATION" ) );
        assertNotNull( env.get( "HTTP_CONTENT_MD5" ) );
        assertNotNull( env.get( "HTTP_CONTENT_RANGE" ) );
        assertNotNull( env.get( "HTTP_CONTENT_TRANSFER_ENCODING" ) );
        assertNotNull( env.get( "CONTENT_TYPE" ) );
        assertNotNull( env.get( "HTTP_COOKIE" ) );
        assertNotNull( env.get( "HTTP_DATE" ) );
        assertNotNull( env.get( "HTTP_ETAG" ) );
        assertNotNull( env.get( "HTTP_EXPECT" ) );
        assertNotNull( env.get( "HTTP_EXPIRES" ) );
        assertNotNull( env.get( "HTTP_FROM" ) );
        assertNotNull( env.get( "HTTP_HOST" ) );
        assertNotNull( env.get( "HTTP_IF_MATCH" ) );
        assertNotNull( env.get( "HTTP_IF_MODIFIED_SINCE" ) );
        assertNotNull( env.get( "HTTP_IF_NONE_MATCH" ) );
        assertNotNull( env.get( "HTTP_IF_RANGE" ) );
        assertNotNull( env.get( "HTTP_IF_UNMODIFIED_SINCE" ) );
        assertNotNull( env.get( "HTTP_LAST_MODIFIED" ) );
        assertNotNull( env.get( "HTTP_LOCATION" ) );
        assertNotNull( env.get( "HTTP_MAX_FORWARDS" ) );
        assertNotNull( env.get( "HTTP_PRAGMA" ) );
        assertNotNull( env.get( "HTTP_PROXY_AUTHENTICATE" ) );
        assertNotNull( env.get( "HTTP_PROXY_AUTHORIZATION" ) );
        assertNotNull( env.get( "HTTP_RANGE" ) );
        assertNotNull( env.get( "HTTP_REFERER" ) );
        assertNotNull( env.get( "HTTP_RETRY_AFTER" ) );
        assertNotNull( env.get( "HTTP_SERVER" ) );
        assertNotNull( env.get( "HTTP_SET_COOKIE" ) );
        assertNotNull( env.get( "HTTP_SET_COOKIE2" ) );
        assertNotNull( env.get( "HTTP_TE" ) );
        assertNotNull( env.get( "HTTP_TRAILER" ) );
        assertNotNull( env.get( "HTTP_TRANSFER_ENCODING" ) );
        assertNotNull( env.get( "HTTP_UPGRADE" ) );
        assertNotNull( env.get( "HTTP_USER_AGENT" ) );
        assertNotNull( env.get( "HTTP_VARY" ) );
        assertNotNull( env.get( "HTTP_VIA" ) );
        assertNotNull( env.get( "HTTP_WARNING" ) );
        assertNotNull( env.get( "HTTP_WWW_AUTHENTICATE" ) );
    }
}
