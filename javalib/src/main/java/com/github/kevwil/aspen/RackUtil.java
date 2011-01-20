package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
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
        if( port != null && port.length() > 0 )
        {
            try
            {
                p = Integer.parseInt( port );
            }
            catch( Exception e )
            {
                e.printStackTrace();
                p = 80;
            }
        }
        return new DumDumCtx( new InetSocketAddress( server, p ) );
    }

    private static class DumDumCtx implements ChannelHandlerContext
    {
        private Channel channel;
        private DumDumCtx( InetSocketAddress address )
        {
            channel = new DumDumChannel( address, new InetSocketAddress( address.getHostName(), 54321 ) );
        }

        public Channel getChannel()
        {
            return channel;
        }

        public ChannelPipeline getPipeline()
        {
            return null;
        }

        public String getName()
        {
            return null;
        }

        public ChannelHandler getHandler()
        {
            return null;
        }

        public boolean canHandleUpstream()
        {
            return false;
        }

        public boolean canHandleDownstream()
        {
            return false;
        }

        public void sendUpstream( final ChannelEvent channelEvent )
        {
        }

        public void sendDownstream( final ChannelEvent channelEvent )
        {
        }

        public Object getAttachment()
        {
            return null;
        }

        public void setAttachment( final Object o )
        {
        }
    }

    private static class DumDumChannel implements Channel
    {
        private SocketAddress localAddress;
        private SocketAddress remoteAddress;

        private DumDumChannel( final SocketAddress localAddress, final SocketAddress remoteAddress )
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

        public boolean isBound()
        {
            return false;
        }

        public boolean isConnected()
        {
            return false;
        }

        public SocketAddress getLocalAddress()
        {
            return localAddress;
        }

        public SocketAddress getRemoteAddress()
        {
            return remoteAddress;
        }

        public ChannelFuture write( final Object o )
        {
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

        public ChannelFuture getCloseFuture()
        {
            return null;
        }

        public int getInterestOps()
        {
            return 0;
        }

        public boolean isReadable()
        {
            return false;
        }

        public boolean isWritable()
        {
            return false;
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
        env.put( "REQUEST_METHOD", request.getMethod().toString() );
    }

    static void doUriRelated( final ChannelHandlerContext ctx, final HttpRequest request, final RubyHash env )
    {
        try
        {
            URI uri = new URI( buildUriString( ctx, request ) );
            env.put( "QUERY_STRING", uri.getQuery() == null ? "" : uri.getQuery() );
            env.put( "PATH_INFO", uri.getPath() );
            if( uri.getHost() != null && uri.getHost().length() > 0 )
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
        if( request.getUri().contains( "://" ) )
        {
            String uri = request.getUri();
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
        if( request.containsHeader( HttpHeaders.Names.HOST ) )
        {
            sb.append( request.getHeader( HttpHeaders.Names.HOST ) );
        }
        else
        {
            InetSocketAddress serverSocket = (InetSocketAddress) ctx.getChannel().getLocalAddress();
            sb.append( serverSocket.getHostName() );
            int port = serverSocket.getPort();
            if( port != 80 )
            {
                sb.append( ":" );
                sb.append( Integer.toString( port ) );
            }
        }
        sb.append( request.getUri() );
        return sb.toString();
    }

    private static void doAccept( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ACCEPT ) )
        {
            env.put( "HTTP_ACCEPT", request.getHeader( HttpHeaders.Names.ACCEPT ) );
        }
    }

    private static void doAcceptCharset( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ACCEPT_CHARSET ) )
        {
            env.put( "HTTP_ACCEPT_CHARSET", request.getHeader( HttpHeaders.Names.ACCEPT_CHARSET ) );
        }
    }

    private static void doAcceptEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ACCEPT_ENCODING ) )
        {
            env.put( "HTTP_ACCEPT_ENCODING", request.getHeader( HttpHeaders.Names.ACCEPT_ENCODING ) );
        }
    }

    private static void doAcceptLanguage( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ACCEPT_LANGUAGE ) )
        {
            env.put( "HTTP_ACCEPT_LANGUAGE", request.getHeader( HttpHeaders.Names.ACCEPT_LANGUAGE ) );
        }
    }

    private static void doAcceptRanges( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ACCEPT_RANGES ) )
        {
            env.put( "HTTP_ACCEPT_RANGES", request.getHeader( HttpHeaders.Names.ACCEPT_RANGES ) );
        }
    }

    private static void doAge( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.AGE ) )
        {
            env.put( "HTTP_AGE", request.getHeader( HttpHeaders.Names.AGE ) );
        }
    }

    private static void doAllow( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ALLOW ) )
        {
            env.put( "HTTP_ALLOW", request.getHeader( HttpHeaders.Names.ALLOW ) );
        }
    }

    private static void doAuthorization( final HttpRequest request, final RubyHash env )
    {
        env.put( "AUTH_TYPE", "" );
        if( request.containsHeader( HttpHeaders.Names.AUTHORIZATION ) )
        {
            String[] auth = request.getHeader( HttpHeaders.Names.AUTHORIZATION ).split( " " );
            if( auth.length > 1 )
            {
                env.put( "AUTH_TYPE", auth[0] );
                env.put( "REMOTE_USER", auth[1] );
            }
            env.put( "HTTP_AUTHORIZATION", request.getHeader( HttpHeaders.Names.AUTHORIZATION ) );
        }
    }

    private static void doCacheControl( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CACHE_CONTROL ) )
        {
            env.put( "HTTP_CACHE_CONTROL", request.getHeader( HttpHeaders.Names.CACHE_CONTROL ) );
        }
    }

    private static void doConnection( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONNECTION ) )
        {
            env.put( "HTTP_CONNECTION", request.getHeader( HttpHeaders.Names.CONNECTION ) );
        }
    }

    private static void doContentEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_ENCODING ) )
        {
            env.put( "HTTP_CONTENT_ENCODING", request.getHeader( HttpHeaders.Names.CONTENT_ENCODING ) );
        }
    }

    private static void doContentLanguage( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_LANGUAGE ) )
        {
            env.put( "HTTP_CONTENT_LANGUAGE", request.getHeader( HttpHeaders.Names.CONTENT_LANGUAGE ) );
        }
    }

    private static void doContentLength( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_LENGTH ) )
        {
            String value = request.getHeader( HttpHeaders.Names.CONTENT_LENGTH );
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
        if( request.containsHeader( HttpHeaders.Names.CONTENT_LOCATION ) )
        {
            env.put( "HTTP_CONTENT_LOCATION", request.getHeader( HttpHeaders.Names.CONTENT_LOCATION ) );
        }
    }

    private static void doContentMd5( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_MD5 ) )
        {
            env.put( "HTTP_CONTENT_MD5", request.getHeader( HttpHeaders.Names.CONTENT_MD5 ) );
        }
    }

    private static void doContentRange( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_RANGE ) )
        {
            env.put( "HTTP_CONTENT_RANGE", request.getHeader( HttpHeaders.Names.CONTENT_RANGE ) );
        }
    }

    private static void doContentTransferEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_TRANSFER_ENCODING ) )
        {
            env.put( "HTTP_CONTENT_TRANSFER_ENCODING", request.getHeader( HttpHeaders.Names.CONTENT_TRANSFER_ENCODING ) );
        }
    }

    private static void doContentType( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_TYPE ) )
        {
                // Rack spec says not to include HTTP_CONTENT_TYPE, but CONTENT_TYPE is OK
            env.put( "CONTENT_TYPE", request.getHeader( HttpHeaders.Names.CONTENT_TYPE ) );
        }
    }

    private static void doCookie( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.COOKIE ) )
        {
            env.put( "HTTP_COOKIE", request.getHeader( HttpHeaders.Names.COOKIE ) );
        }
    }

    private static void doDate( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.DATE ) )
        {
            env.put( "HTTP_DATE", request.getHeader( HttpHeaders.Names.DATE ) );
        }
    }

    private static void doEtag( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.ETAG ) )
        {
            env.put( "HTTP_ETAG", request.getHeader( HttpHeaders.Names.ETAG ) );
        }
    }

    private static void doExpect( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.EXPECT ) )
        {
            env.put( "HTTP_EXPECT", request.getHeader( HttpHeaders.Names.EXPECT ) );
        }
    }

    private static void doExpires( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.EXPIRES ) )
        {
            env.put( "HTTP_EXPIRES", request.getHeader( HttpHeaders.Names.EXPIRES ) );
        }
    }

    private static void doFrom( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.FROM ) )
        {
            env.put( "HTTP_FROM", request.getHeader( HttpHeaders.Names.FROM ) );
        }
    }

    private static void doHost( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.HOST ) )
        {
            env.put( "HTTP_HOST", request.getHeader( HttpHeaders.Names.HOST ) );
        }
    }

    private static void doIfMatch( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.IF_MATCH ) )
        {
            env.put( "HTTP_IF_MATCH", request.getHeader( HttpHeaders.Names.IF_MATCH ) );
        }
    }

    private static void doIfModifiedSince( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.IF_MODIFIED_SINCE ) )
        {
            env.put( "HTTP_IF_MODIFIED_SINCE", request.getHeader( HttpHeaders.Names.IF_MODIFIED_SINCE ) );
        }
    }

    private static void doIfNoneMatch( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.IF_NONE_MATCH ) )
        {
            env.put( "HTTP_IF_NONE_MATCH", request.getHeader( HttpHeaders.Names.IF_NONE_MATCH ) );
        }
    }

    private static void doIfRange( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.IF_RANGE ) )
        {
            env.put( "HTTP_IF_RANGE", request.getHeader( HttpHeaders.Names.IF_RANGE) );
        }
    }

    private static void doIfUnmodifiedSince( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.IF_UNMODIFIED_SINCE ) )
        {
            env.put( "HTTP_IF_UNMODIFIED_SINCE", request.getHeader( HttpHeaders.Names.IF_UNMODIFIED_SINCE ) );
        }
    }

    private static void doLastModified( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.LAST_MODIFIED ) )
        {
            env.put( "HTTP_LAST_MODIFIED", request.getHeader( HttpHeaders.Names.LAST_MODIFIED ) );
        }
    }

    private static void doLocation( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.LOCATION ) )
        {
            env.put( "HTTP_LOCATION", request.getHeader( HttpHeaders.Names.LOCATION ) );
        }
    }

    private static void doMaxForwards( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.MAX_FORWARDS ) )
        {
            env.put( "HTTP_MAX_FORWARDS", request.getHeader( HttpHeaders.Names.MAX_FORWARDS ) );
        }
    }

    private static void doPragma( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.PRAGMA ) )
        {
            env.put( "HTTP_PRAGMA", request.getHeader( HttpHeaders.Names.PRAGMA ) );
        }
    }

    private static void doProxyAuthenticate( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.PROXY_AUTHENTICATE ) )
        {
            env.put( "HTTP_PROXY_AUTHENTICATE", request.getHeader( HttpHeaders.Names.PROXY_AUTHENTICATE ) );
        }
    }

    private static void doProxyAuthorization( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.PROXY_AUTHORIZATION ) )
        {
            env.put( "HTTP_PROXY_AUTHORIZATION", request.getHeader( HttpHeaders.Names.PROXY_AUTHORIZATION ) );
        }
    }

    private static void doRange( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.RANGE ) )
        {
            env.put( "HTTP_RANGE", request.getHeader( HttpHeaders.Names.RANGE ) );
        }
    }

    private static void doReferer( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.REFERER ) )
        {
            env.put( "HTTP_REFERER", request.getHeader( HttpHeaders.Names.REFERER ) );
        }
    }

    private static void doRetryAfter( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.RETRY_AFTER ) )
        {
            env.put( "HTTP_RETRY_AFTER", request.getHeader( HttpHeaders.Names.RETRY_AFTER ) );
        }
    }

    private static void doServer( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.SERVER ) )
        {
            env.put( "HTTP_SERVER", request.getHeader( HttpHeaders.Names.SERVER ) );
        }
    }

    private static void doSetCookie( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.SET_COOKIE ) )
        {
            env.put( "HTTP_SET_COOKIE", request.getHeader( HttpHeaders.Names.SET_COOKIE ) );
        }
    }

    private static void doSetCookie2( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.SET_COOKIE2 ) )
        {
            env.put( "HTTP_SET_COOKIE2", request.getHeader( HttpHeaders.Names.SET_COOKIE2 ) );
        }
    }

    private static void doTe( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.TE ) )
        {
            env.put( "HTTP_TE", request.getHeader( HttpHeaders.Names.TE ) );
        }
    }

    private static void doTrailer( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.TRAILER ) )
        {
            env.put( "HTTP_TRAILER", request.getHeader( HttpHeaders.Names.TRAILER ) );
        }
    }

    private static void doTransferEncoding( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.TRANSFER_ENCODING ) )
        {
            env.put( "HTTP_TRANSFER_ENCODING", request.getHeader( HttpHeaders.Names.TRANSFER_ENCODING ) );
        }
    }

    private static void doUpgrade( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.UPGRADE ) )
        {
            env.put( "HTTP_UPGRADE", request.getHeader( HttpHeaders.Names.UPGRADE ) );
        }
    }

    private static void doUserAgent( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.USER_AGENT ) )
        {
            env.put( "HTTP_USER_AGENT", request.getHeader( HttpHeaders.Names.USER_AGENT ) );
        }
    }

    private static void doVary( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.VARY ) )
        {
            env.put( "HTTP_VARY", request.getHeader( HttpHeaders.Names.VARY ) );
        }
    }

    private static void doVia( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.VIA ) )
        {
            env.put( "HTTP_VIA", request.getHeader( HttpHeaders.Names.VIA ) );
        }
    }

    private static void doWarning( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.WARNING ) )
        {
            env.put( "HTTP_WARNING", request.getHeader( HttpHeaders.Names.WARNING ) );
        }
    }

    private static void doWwwAuthenticate( final HttpRequest request, final RubyHash env )
    {
        if( request.containsHeader( HttpHeaders.Names.WWW_AUTHENTICATE ) )
        {
            env.put( "HTTP_WWW_AUTHENTICATE", request.getHeader( HttpHeaders.Names.WWW_AUTHENTICATE ) );
        }
    }
    
}
