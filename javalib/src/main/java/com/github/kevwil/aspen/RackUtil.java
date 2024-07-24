package com.github.kevwil.aspen;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.ServerSocketChannelConfig;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import org.jruby.RubyHash;

import java.net.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author kevwil
 * @since Nov 25, 2009
 */
public final class RackUtil
{
    private RackUtil(){}

    public static ChannelHandlerContext buildDummyChannelHandlerContext( final String server, final String port )
    {
        int p = 80;
        if( port != null && !port.isEmpty())
        {
            try
            {
                p = Integer.parseInt( port );
            }
            catch( Exception e )
            {
                e.printStackTrace(System.err);
//                p = 80;
            }
        }
        return new DumDumCtx( new InetSocketAddress( server, p ) );
    }

    private static class DumDumCtx implements ChannelHandlerContext
    {
        private final Channel channel;
        private DumDumCtx( InetSocketAddress address )
        {
            channel = new DumDumChannel( address, new InetSocketAddress( address.getHostName(), 54321 ) );
        }

        @Override
        public Channel channel() {
            return channel;
        }

        @Override
        public EventExecutor executor() {
            return null;
        }

        @Override
        public String name() {
            return "";
        }

        @Override
        public ChannelHandler handler() {
            return null;
        }

        @Override
        public boolean isRemoved() {
            return false;
        }

        @Override
        public ChannelHandlerContext fireChannelRegistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelUnregistered() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelActive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelInactive() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireExceptionCaught(Throwable throwable) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireUserEventTriggered(Object o) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelRead(Object o) {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelReadComplete() {
            return null;
        }

        @Override
        public ChannelHandlerContext fireChannelWritabilityChanged() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress socketAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
            return null;
        }

        @Override
        public ChannelFuture disconnect() {
            return null;
        }

        @Override
        public ChannelFuture close() {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelHandlerContext read() {
            return null;
        }

        @Override
        public ChannelFuture write(Object o) {
            return null;
        }

        @Override
        public ChannelFuture write(Object o, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelHandlerContext flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object o) {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable throwable) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
            return false;
        }
    }

    private static class DumDumChannel implements ServerSocketChannel
    {
        private final InetSocketAddress localAddress;
        private final InetSocketAddress remoteAddress;

        private DumDumChannel( final InetSocketAddress localAddress, final InetSocketAddress remoteAddress )
        {
            this.localAddress = localAddress;
            this.remoteAddress = remoteAddress;
        }

        public Integer getId()
        {
            return null;
        }

        public ChannelFactory getFactory()
        {
            return null;
        }

        public Channel getParent()
        {
            return null;
        }

        public ChannelConfig getConfig()
        {
            return null;
        }

        public ChannelPipeline getPipeline()
        {
            return null;
        }

        public boolean isOpen()
        {
            return false;
        }

        @Override
        public boolean isRegistered() {
            return false;
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public ChannelMetadata metadata() {
            return null;
        }

        public ChannelFuture write( final Object o )
        {
            return null;
        }

        @Override
        public ChannelFuture write(Object o, ChannelPromise channelPromise) {
            return null;
        }

        public ChannelFuture write( final Object o, final SocketAddress socketAddress )
        {
            return null;
        }

        public ChannelFuture bind( final SocketAddress socketAddress )
        {
            return null;
        }

        public ChannelFuture connect( final SocketAddress socketAddress )
        {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1) {
            return null;
        }

        public ChannelFuture disconnect()
        {
            return null;
        }

        public ChannelFuture unbind()
        {
            return null;
        }

        public ChannelFuture close()
        {
            return null;
        }

        @Override
        public ChannelFuture deregister() {
            return null;
        }

        @Override
        public ChannelFuture bind(SocketAddress socketAddress, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture connect(SocketAddress socketAddress, SocketAddress socketAddress1, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture disconnect(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture close(ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture deregister(ChannelPromise channelPromise) {
            return null;
        }

        public ChannelFuture getCloseFuture()
        {
            return null;
        }

        public boolean isWritable()
        {
            return false;
        }

        @Override
        public long bytesBeforeUnwritable() {
            return 0;
        }

        @Override
        public long bytesBeforeWritable() {
            return 0;
        }

        @Override
        public Unsafe unsafe() {
            return null;
        }

        @Override
        public ChannelPipeline pipeline() {
            return null;
        }

        @Override
        public ByteBufAllocator alloc() {
            return null;
        }

        @Override
        public Channel read() {
            return null;
        }

        @Override
        public Channel flush() {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object o, ChannelPromise channelPromise) {
            return null;
        }

        @Override
        public ChannelFuture writeAndFlush(Object o) {
            return null;
        }

        @Override
        public ChannelPromise newPromise() {
            return null;
        }

        @Override
        public ChannelProgressivePromise newProgressivePromise() {
            return null;
        }

        @Override
        public ChannelFuture newSucceededFuture() {
            return null;
        }

        @Override
        public ChannelFuture newFailedFuture(Throwable throwable) {
            return null;
        }

        @Override
        public ChannelPromise voidPromise() {
            return null;
        }

        public ChannelFuture setInterestOps( final int i )
        {
            return null;
        }

        public ChannelFuture setReadable( final boolean b )
        {
            return null;
        }

        public int compareTo( final Channel o )
        {
            return 0;
        }

        @Override
        public ChannelId id() {
            return null;
        }

        @Override
        public EventLoop eventLoop() {
            return null;
        }

        @Override
        public Channel parent() {
            return null;
        }

        @Override
        public ServerSocketChannelConfig config() {
            return null;
        }

        @Override
        public InetSocketAddress localAddress() {
            return localAddress;
        }

        @Override
        public InetSocketAddress remoteAddress() {
            return remoteAddress;
        }

        @Override
        public ChannelFuture closeFuture() {
            return null;
        }

        @Override
        public <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
            return null;
        }

        @Override
        public <T> boolean hasAttr(AttributeKey<T> attributeKey) {
            return false;
        }
    }

    public static void parseHeaders( final ChannelHandlerContext ctx, final HttpRequest request, final RubyHash env )
    {
        env.put( "SCRIPT_NAME", "" );
        doMethod( request, env );
        doUriRelated( ctx, request, env );
        doAccept( request, env );
        doAcceptCharset( request, env );
        doAcceptEncoding( request, env );
        doAcceptLanguage( request, env );
        doAcceptRanges( request, env );
        doAge( request, env );
        doAllow( request, env );
        doAuthorization( request, env );
        doCacheControl( request, env );
        doConnection( request, env );
        doContentEncoding( request, env );
        doContentLanguage( request, env );
        doContentLength( request, env );
        doContentLocation( request, env );
        doContentMd5( request, env );
        doContentRange( request, env );
        doContentTransferEncoding( request, env );
        doContentType( request, env );
        doCookie( request, env );
        doDate( request, env );
        doEtag( request, env );
        doExpect( request, env );
        doExpires( request, env );
        doFrom( request, env );
        doHost( request, env );
        doIfMatch( request, env );
        doIfModifiedSince( request, env );
        doIfNoneMatch( request, env );
        doIfRange( request, env );
        doIfUnmodifiedSince( request, env );
        doLastModified( request, env );
        doLocation( request, env );
        doMaxForwards( request, env );
        doPragma( request, env );
        doProxyAuthenticate( request, env );
        doProxyAuthorization( request, env );
        doRange( request, env );
        doReferer( request, env );
        doRetryAfter( request, env );
        doServer( request, env );
        doSetCookie( request, env );
        doSetCookie2( request, env );
        doTe( request, env );
        doTrailer( request, env );
        doTransferEncoding( request, env );
        doUpgrade( request, env );
        doUserAgent( request, env );
        doVary( request, env );
        doVia( request, env );
        doWarning( request, env );
        doWwwAuthenticate( request, env );
    }

    private static void doMethod( final HttpRequest request, final RubyHash env )
    {
        env.put( "REQUEST_METHOD", request.method().toString() );
    }

    static void doUriRelated( final ChannelHandlerContext ctx, final HttpRequest request, final RubyHash env )
    {
        try
        {
            URI uri = new URI( buildUriString( ctx, request ) );
            env.put( "QUERY_STRING", uri.getQuery() == null ? "" : uri.getQuery() );
            env.put( "PATH_INFO", uri.getPath() );
            if( uri.getHost() != null && !uri.getHost().isEmpty())
            {
                env.put( "SERVER_NAME", uri.getHost() );
            }
            if( uri.getPort() != -1 )
            {
                env.put( "SERVER_PORT", Integer.toString( uri.getPort() ) );
            }
            else
            {
                env.put( "SERVER_PORT", "80" );
            }
        }
        catch( URISyntaxException e )
        {
            e.printStackTrace( System.err );
            env.put( "QUERY_STRING", "" );
            env.put( "PATH_INFO", "/" ); //??
            env.put( "SERVER_NAME", "localhost" );
        }
    }

    private static String buildUriString( final ChannelHandlerContext ctx, final HttpRequest request )
    {
        if( request.uri().contains( "://" ) )
        {
            String uri = request.uri();
            Pattern pattern = Pattern.compile("http:\\/\\/(.*)\\/(.*)");
            Matcher matcher = pattern.matcher(uri);
            if( matcher.matches() && matcher.groupCount() > 1 )
            {
                String hostAndPort = matcher.group(1);
                String path = matcher.group(2);
                if( hostAndPort.contains(":") )
                {
                    return uri;
                }
                else
                {
                    return "http://" + hostAndPort + ":80/" + path;
                }
            }
        }
        StringBuilder sb = new StringBuilder();
        sb.append( "http://" );
        if( request.headers().contains( HttpHeaderNames.HOST ) )
        {
            sb.append( request.headers().contains( HttpHeaderNames.HOST ) );
        }
        else
        {
            InetSocketAddress serverSocket = (InetSocketAddress) ctx.channel().localAddress();
            sb.append( serverSocket.getHostName() );
            int port = serverSocket.getPort();
            if( port != 80 )
            {
                sb.append( ":" );
                sb.append( port );
            }
        }
        sb.append( request.uri() );
        return sb.toString();
    }

    private static void doAccept( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ACCEPT ) )
        {
            env.put( "HTTP_ACCEPT", request.headers().get( HttpHeaderNames.ACCEPT ) );
        }
    }

    private static void doAcceptCharset( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ACCEPT_CHARSET ) )
        {
            env.put( "HTTP_ACCEPT_CHARSET", request.headers().get( HttpHeaderNames.ACCEPT_CHARSET ) );
        }
    }

    private static void doAcceptEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ACCEPT_ENCODING ) )
        {
            env.put( "HTTP_ACCEPT_ENCODING", request.headers().get( HttpHeaderNames.ACCEPT_ENCODING ) );
        }
    }

    private static void doAcceptLanguage( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ACCEPT_LANGUAGE ) )
        {
            env.put( "HTTP_ACCEPT_LANGUAGE", request.headers().get( HttpHeaderNames.ACCEPT_LANGUAGE ) );
        }
    }

    private static void doAcceptRanges( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ACCEPT_RANGES ) )
        {
            env.put( "HTTP_ACCEPT_RANGES", request.headers().get( HttpHeaderNames.ACCEPT_RANGES ) );
        }
    }

    private static void doAge( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.AGE ) )
        {
            env.put( "HTTP_AGE", request.headers().get( HttpHeaderNames.AGE ) );
        }
    }

    private static void doAllow( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ALLOW ) )
        {
            env.put( "HTTP_ALLOW", request.headers().get( HttpHeaderNames.ALLOW ) );
        }
    }

    private static void doAuthorization( final HttpRequest request, final RubyHash env )
    {
        env.put( "AUTH_TYPE", "" );
        if( request.headers().contains( HttpHeaderNames.AUTHORIZATION ) )
        {
            String[] auth = request.headers().get( HttpHeaderNames.AUTHORIZATION ).split( " " );
            if( auth.length > 1 )
            {
                env.put( "AUTH_TYPE", auth[0] );
                env.put( "REMOTE_USER", auth[1] );
            }
            env.put( "HTTP_AUTHORIZATION", request.headers().get( HttpHeaderNames.AUTHORIZATION ) );
        }
    }

    private static void doCacheControl( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CACHE_CONTROL ) )
        {
            env.put( "HTTP_CACHE_CONTROL", request.headers().get( HttpHeaderNames.CACHE_CONTROL ) );
        }
    }

    private static void doConnection( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONNECTION ) )
        {
            env.put( "HTTP_CONNECTION", request.headers().get( HttpHeaderNames.CONNECTION ) );
        }
    }

    private static void doContentEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_ENCODING ) )
        {
            env.put( "HTTP_CONTENT_ENCODING", request.headers().get( HttpHeaderNames.CONTENT_ENCODING ) );
        }
    }

    private static void doContentLanguage( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_LANGUAGE ) )
        {
            env.put( "HTTP_CONTENT_LANGUAGE", request.headers().get( HttpHeaderNames.CONTENT_LANGUAGE ) );
        }
    }

    private static void doContentLength( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_LENGTH ) )
        {
            String value = request.headers().get( HttpHeaderNames.CONTENT_LENGTH );
            if( ! value.matches("^\\d+$") )
            {
                throw new RuntimeException("CONTENT_LENGTH must consist of digits only.");
            }
            else
            {
                // Rack spec says not to include HTTP_CONTENT_LENGTH, but CONTENT_LENGTH is OK
                env.put("CONTENT_LENGTH", value);
            }
        }
    }

    private static void doContentLocation( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_LOCATION ) )
        {
            env.put( "HTTP_CONTENT_LOCATION", request.headers().get( HttpHeaderNames.CONTENT_LOCATION ) );
        }
    }

    private static void doContentMd5( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_MD5 ) )
        {
            env.put( "HTTP_CONTENT_MD5", request.headers().get( HttpHeaderNames.CONTENT_MD5 ) );
        }
    }

    private static void doContentRange( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_RANGE ) )
        {
            env.put( "HTTP_CONTENT_RANGE", request.headers().get( HttpHeaderNames.CONTENT_RANGE ) );
        }
    }

    private static void doContentTransferEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_TRANSFER_ENCODING ) )
        {
            env.put( "HTTP_CONTENT_TRANSFER_ENCODING", request.headers().get( HttpHeaderNames.CONTENT_TRANSFER_ENCODING ) );
        }
    }

    private static void doContentType( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.CONTENT_TYPE ) )
        {
                // Rack spec says not to include HTTP_CONTENT_TYPE, but CONTENT_TYPE is OK
            env.put( "CONTENT_TYPE", request.headers().get( HttpHeaderNames.CONTENT_TYPE ) );
        }
    }

    private static void doCookie( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.COOKIE ) )
        {
            env.put( "HTTP_COOKIE", request.headers().get( HttpHeaderNames.COOKIE ) );
        }
    }

    private static void doDate( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.DATE ) )
        {
            env.put( "HTTP_DATE", request.headers().get( HttpHeaderNames.DATE ) );
        }
    }

    private static void doEtag( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.ETAG ) )
        {
            env.put( "HTTP_ETAG", request.headers().get( HttpHeaderNames.ETAG ) );
        }
    }

    private static void doExpect( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.EXPECT ) )
        {
            env.put( "HTTP_EXPECT", request.headers().get( HttpHeaderNames.EXPECT ) );
        }
    }

    private static void doExpires( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.EXPIRES ) )
        {
            env.put( "HTTP_EXPIRES", request.headers().get( HttpHeaderNames.EXPIRES ) );
        }
    }

    private static void doFrom( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.FROM ) )
        {
            env.put( "HTTP_FROM", request.headers().get( HttpHeaderNames.FROM ) );
        }
    }

    private static void doHost( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.HOST ) )
        {
            env.put( "HTTP_HOST", request.headers().get( HttpHeaderNames.HOST ) );
        }
    }

    private static void doIfMatch( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.IF_MATCH ) )
        {
            env.put( "HTTP_IF_MATCH", request.headers().get( HttpHeaderNames.IF_MATCH ) );
        }
    }

    private static void doIfModifiedSince( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.IF_MODIFIED_SINCE ) )
        {
            env.put( "HTTP_IF_MODIFIED_SINCE", request.headers().get( HttpHeaderNames.IF_MODIFIED_SINCE ) );
        }
    }

    private static void doIfNoneMatch( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.IF_NONE_MATCH ) )
        {
            env.put( "HTTP_IF_NONE_MATCH", request.headers().get( HttpHeaderNames.IF_NONE_MATCH ) );
        }
    }

    private static void doIfRange( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.IF_RANGE ) )
        {
            env.put( "HTTP_IF_RANGE", request.headers().get( HttpHeaderNames.IF_RANGE) );
        }
    }

    private static void doIfUnmodifiedSince( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.IF_UNMODIFIED_SINCE ) )
        {
            env.put( "HTTP_IF_UNMODIFIED_SINCE", request.headers().get( HttpHeaderNames.IF_UNMODIFIED_SINCE ) );
        }
    }

    private static void doLastModified( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.LAST_MODIFIED ) )
        {
            env.put( "HTTP_LAST_MODIFIED", request.headers().get( HttpHeaderNames.LAST_MODIFIED ) );
        }
    }

    private static void doLocation( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.LOCATION ) )
        {
            env.put( "HTTP_LOCATION", request.headers().get( HttpHeaderNames.LOCATION ) );
        }
    }

    private static void doMaxForwards( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.MAX_FORWARDS ) )
        {
            env.put( "HTTP_MAX_FORWARDS", request.headers().get( HttpHeaderNames.MAX_FORWARDS ) );
        }
    }

    private static void doPragma( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.PRAGMA ) )
        {
            env.put( "HTTP_PRAGMA", request.headers().get( HttpHeaderNames.PRAGMA ) );
        }
    }

    private static void doProxyAuthenticate( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.PROXY_AUTHENTICATE ) )
        {
            env.put( "HTTP_PROXY_AUTHENTICATE", request.headers().get( HttpHeaderNames.PROXY_AUTHENTICATE ) );
        }
    }

    private static void doProxyAuthorization( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.PROXY_AUTHORIZATION ) )
        {
            env.put( "HTTP_PROXY_AUTHORIZATION", request.headers().get( HttpHeaderNames.PROXY_AUTHORIZATION ) );
        }
    }

    private static void doRange( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.RANGE ) )
        {
            env.put( "HTTP_RANGE", request.headers().get( HttpHeaderNames.RANGE ) );
        }
    }

    private static void doReferer( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.REFERER ) )
        {
            env.put( "HTTP_REFERER", request.headers().get( HttpHeaderNames.REFERER ) );
        }
    }

    private static void doRetryAfter( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.RETRY_AFTER ) )
        {
            env.put( "HTTP_RETRY_AFTER", request.headers().get( HttpHeaderNames.RETRY_AFTER ) );
        }
    }

    private static void doServer( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.SERVER ) )
        {
            env.put( "HTTP_SERVER", request.headers().get( HttpHeaderNames.SERVER ) );
        }
    }

    private static void doSetCookie( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.SET_COOKIE ) )
        {
            env.put( "HTTP_SET_COOKIE", request.headers().get( HttpHeaderNames.SET_COOKIE ) );
        }
    }

    private static void doSetCookie2( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.SET_COOKIE2 ) )
        {
            env.put( "HTTP_SET_COOKIE2", request.headers().get( HttpHeaderNames.SET_COOKIE2 ) );
        }
    }

    private static void doTe( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.TE ) )
        {
            env.put( "HTTP_TE", request.headers().get( HttpHeaderNames.TE ) );
        }
    }

    private static void doTrailer( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.TRAILER ) )
        {
            env.put( "HTTP_TRAILER", request.headers().get( HttpHeaderNames.TRAILER ) );
        }
    }

    private static void doTransferEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.TRANSFER_ENCODING ) )
        {
            env.put( "HTTP_TRANSFER_ENCODING", request.headers().get( HttpHeaderNames.TRANSFER_ENCODING ) );
        }
    }

    private static void doUpgrade( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.UPGRADE ) )
        {
            env.put( "HTTP_UPGRADE", request.headers().get( HttpHeaderNames.UPGRADE ) );
        }
    }

    private static void doUserAgent( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.USER_AGENT ) )
        {
            env.put( "HTTP_USER_AGENT", request.headers().get( HttpHeaderNames.USER_AGENT ) );
        }
    }

    private static void doVary( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.VARY ) )
        {
            env.put( "HTTP_VARY", request.headers().get( HttpHeaderNames.VARY ) );
        }
    }

    private static void doVia( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.VIA ) )
        {
            env.put( "HTTP_VIA", request.headers().get( HttpHeaderNames.VIA ) );
        }
    }

    private static void doWarning( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.WARNING ) )
        {
            env.put( "HTTP_WARNING", request.headers().get( HttpHeaderNames.WARNING ) );
        }
    }

    private static void doWwwAuthenticate( final HttpRequest request, final RubyHash env )
    {
        if( request.headers().contains( HttpHeaderNames.WWW_AUTHENTICATE ) )
        {
            env.put( "HTTP_WWW_AUTHENTICATE", request.headers().get( HttpHeaderNames.WWW_AUTHENTICATE ) );
        }
    }
    
}
