package com.github.kevwil.aspen;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
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
        ctx = RackUtil.buildDummyChannelHandlerContext( server, port );
        r = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://"+server+":"+port+"/" );
        env = RubyHash.newHash( ruby );
    }
    @Test
    public void shouldCreateDummyChannelHandlerContextWithLocalServerAddress() throws Exception
    {
        assertNotNull( ctx );
        assertNotNull( ctx.channel() );
        assertNotNull( ctx.channel().localAddress() );
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
        assertEquals( server, socketAddress.getHostName() );
        assertEquals( port, Integer.toString( socketAddress.getPort() ) );
    }

    @Test
    public void shouldUsePort80() throws Exception
    {
        ctx = RackUtil.buildDummyChannelHandlerContext( server, null );
        assertNotNull( ctx );
        assertNotNull( ctx.channel() );
        assertNotNull( ctx.channel().localAddress() );
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().localAddress();
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
        r.headers().add( HttpHeaderNames.HOST, server+":"+port );

        RackUtil.doUriRelated( null, r, env );

        assertEquals( "", env.get( "QUERY_STRING" ) );
        assertEquals( "/", env.get( "PATH_INFO" ) );
        assertEquals( server, env.get( "SERVER_NAME" ) );
        assertEquals( port, env.get( "SERVER_PORT" ) );
    }

    @Test
    public void shouldParseServerPortWhenNoneGiven() throws Exception
    {
        ctx = RackUtil.buildDummyChannelHandlerContext( server, null );
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
        r.headers().add( HttpHeaderNames.ACCEPT, "*/*" );
        r.headers().add( HttpHeaderNames.ACCEPT_CHARSET, "accept-charset" );
        r.headers().add( HttpHeaderNames.ACCEPT_ENCODING, "accept-encoding" );
        r.headers().add( HttpHeaderNames.ACCEPT_LANGUAGE, "accept-language" );
        r.headers().add( HttpHeaderNames.ACCEPT_RANGES, "accept-ranges" );
        r.headers().add( HttpHeaderNames.AGE, "age" );
        r.headers().add( HttpHeaderNames.ALLOW, "allow" );
        r.headers().add( HttpHeaderNames.AUTHORIZATION, "authorization" );
        r.headers().add( HttpHeaderNames.CACHE_CONTROL, "cache_control" );
        r.headers().add( HttpHeaderNames.CONNECTION, "connection" );
        r.headers().add( HttpHeaderNames.CONTENT_ENCODING, "content_encoding" );
        r.headers().add( HttpHeaderNames.CONTENT_LANGUAGE, "content_language" );
        r.headers().add( HttpHeaderNames.CONTENT_LENGTH, "12" );
        r.headers().add( HttpHeaderNames.CONTENT_LOCATION, "content_location" );
        r.headers().add( HttpHeaderNames.CONTENT_MD5, "content_md5" );
        r.headers().add( HttpHeaderNames.CONTENT_RANGE, "content_range" );
        r.headers().add( HttpHeaderNames.CONTENT_TRANSFER_ENCODING, "content_transfer_encoding" );
        r.headers().add( HttpHeaderNames.CONTENT_TYPE, "content_type" );
        r.headers().add( HttpHeaderNames.COOKIE, "cookie" );
        r.headers().add( HttpHeaderNames.DATE, "date" );
        r.headers().add( HttpHeaderNames.ETAG, "etag" );
        r.headers().add( HttpHeaderNames.EXPECT, "expect" );
        r.headers().add( HttpHeaderNames.EXPIRES, "expires" );
        r.headers().add( HttpHeaderNames.FROM, "from" );
        r.headers().add( HttpHeaderNames.HOST, "host" );
        r.headers().add( HttpHeaderNames.IF_MATCH, "if-match" );
        r.headers().add( HttpHeaderNames.IF_MODIFIED_SINCE, "if-modified-since" );
        r.headers().add( HttpHeaderNames.IF_NONE_MATCH, "if-none-match" );
        r.headers().add( HttpHeaderNames.IF_RANGE, "if-range" );
        r.headers().add( HttpHeaderNames.IF_UNMODIFIED_SINCE, "if-unmodified-since" );
        r.headers().add( HttpHeaderNames.LAST_MODIFIED, "last-modified" );
        r.headers().add( HttpHeaderNames.LOCATION, "location" );
        r.headers().add( HttpHeaderNames.MAX_FORWARDS, "max-forwards" );
        r.headers().add( HttpHeaderNames.PRAGMA, "pragma" );
        r.headers().add( HttpHeaderNames.PROXY_AUTHENTICATE, "proxy-authenticate" );
        r.headers().add( HttpHeaderNames.PROXY_AUTHORIZATION, "proxy-authorization" );
        r.headers().add( HttpHeaderNames.RANGE, "range" );
        r.headers().add( HttpHeaderNames.REFERER, "referer" );
        r.headers().add( HttpHeaderNames.RETRY_AFTER, "retry-after" );
        r.headers().add( HttpHeaderNames.SERVER, "server" );
        r.headers().add( HttpHeaderNames.SET_COOKIE, "set-cookie" );
        r.headers().add( HttpHeaderNames.SET_COOKIE2, "range" );
        r.headers().add( HttpHeaderNames.TE, "te" );
        r.headers().add( HttpHeaderNames.TRAILER, "trailer" );
        r.headers().add( HttpHeaderNames.TRANSFER_ENCODING, "transfer-encoding" );
        r.headers().add( HttpHeaderNames.UPGRADE, "upgrade" );
        r.headers().add( HttpHeaderNames.USER_AGENT, "user-agent" );
        r.headers().add( HttpHeaderNames.VARY, "vary" );
        r.headers().add( HttpHeaderNames.VIA, "via" );
        r.headers().add( HttpHeaderNames.WARNING, "warning" );
        r.headers().add( HttpHeaderNames.WWW_AUTHENTICATE, "www-authenticate" );

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
