package com.github.kevwil.aspen;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.slf4j.*;

import java.net.*;
import java.util.*;

/**
 * Create a Rack environment to pass out to Rack middleware.
 * @author kevwil
 * @since Jul 1, 2009
 */
public final class RackEnvironmentMaker
{
    private static final List<String> allowedSchemes = Arrays.asList( "http","https" );
    private static final Logger log = LoggerFactory.getLogger( RackEnvironmentMaker.class );

    /**
     * build up a Ruby hash from the request
     * @param ctx the request context
     * @param httpRequest the Netty request
     * @param runtime the current JRuby runtime
     * @return a Ruby hash instance
     * @throws RackException if there's a problem
     */
    public static IRubyObject build( final ChannelHandlerContext ctx, HttpRequest httpRequest, Ruby runtime )
    throws RackException
    {
        RubyHash env = new RubyHash( runtime );
        env.put( "rack.version", getRackVersion( runtime ) );
        URI uri = getUri( ctx, httpRequest );
        log.debug( "built Java URI from request: " + uri );
        assignUrlScheme( uri, env );
        env.put( "rack.multithread", runtime.getFalse() );
        env.put( "rack.multiprocess", runtime.getFalse() );
        env.put( "rack.run_once", runtime.getFalse() );
        env.put( "REQUEST_METHOD", httpRequest.getMethod().toString() );
        env.put( "SCRIPT_NAME", "" );
        assignPathInfo( uri, env );
        env.put( "QUERY_STRING", uri.getQuery() == null ? "" : uri.getQuery() );
        env.put( "SERVER_NAME", uri.getHost() );
        env.put( "SERVER_PORT", uri.getPort() == -1 ? "80" : Integer.toString( uri.getPort() ) );
        parseHttpHeaders( httpRequest, env );

        assignInputStream( httpRequest, env );
        assignOutputStream( env );
        /*
         * TODO:
         * NEED TO ASSIGN SESSION FROM APP FIRST,
         * USE DEFAULT IF NOT ASSIGNED
         *
         */
        System.out.println( "built rack env: " + env );
        return env;
    }

    private static void assignPathInfo( URI uri, RubyHash env )
    throws RackException
    {
        String path = uri.getPath();
        if( path.length() > 0 && !path.startsWith("/") )
        {
            RackException ex = new RackException( "invalid path, must start with '/'" );
            log.error( "error assigning PATH_INFO", ex );
            throw ex;
        }
        else
        {
            env.put( "PATH_INFO", path );
        }
    }

    private static void assignUrlScheme( URI uri, RubyHash env )
    throws RackException
    {
        String urlScheme = uri.getScheme();
        if( urlScheme == null )
        {
            RackException ex = new RackException("no http url scheme found");
            log.error( "error assigning url scheme", ex );
            throw ex;
        }
        if( allowedSchemes.contains( urlScheme ) )
        {
            env.put( "rack.url_scheme", urlScheme );
        }
        else
        {
            RackException ex = new RackException( "invalid url scheme: " + urlScheme );
            log.error( "error assigning url scheme", ex );
            throw ex;
        }
    }

    private static RubyArray getRackVersion( Ruby runtime )
    {
        RubyArray version = RubyArray.newArray( runtime );
        version.add( 1 );
        version.add( 0 );
        return version;
    }

    private static URI getUri( ChannelHandlerContext ctx, HttpRequest request ) throws RackException
    {
        try
        {
            return new URI( buildFullRequest( ctx, request ) );
        }
        catch( URISyntaxException ex )
        {
            log.error( "error parsing request URI", ex );
            throw new RackException( ex );
        }
    }

    private static String buildFullRequest( ChannelHandlerContext ctx, HttpRequest request )
    {
        if( request.getUri().contains( "://" ) )
        {
            return request.getUri();
        }
        else
        {
            // TODO: Netty can do SSL, eventually will need to handle it

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
                sb.append( ":" );
                sb.append( Integer.toString( serverSocket.getPort() ) );
            }
            sb.append( request.getUri() );
            return sb.toString();
        }

    }

    private static void assignInputStream( HttpRequest httpRequest, RubyHash env )
    {
        // Thin uses StringIO.new(body)
        
//        env.put( "rack.input",
//                new RubyIO( env.getRuntime(), new ChannelBufferInputStream( httpRequest.getContent() ) ) );
        env.put( "rack.input", new BufferedRackInput( env.getRuntime(), httpRequest.getContent() ) );
//        ChannelBuffer content = httpRequest.getContent();
//        int len = (int)httpRequest.getContentLength();
//        byte[] buf = new byte[len];
//        content.readBytes( buf, 0, len );
//        new RubyStringIO( env.getRuntime(), RubyString.newString( env.getRuntime(), buf ) );
    }

    private static void assignOutputStream( RubyHash env )
    {
        // Thin uses STDERR
        
//        RubyIO stderr = new RubyIO( env.getRuntime(), env.getRuntime().getStandardError() );
//        env.put( "rack.errors", stderr );
        env.put( "rack.errors", new DefaultRackOutput( env ) );
//        env.put( "rack.errors", env.getRuntime().getGlobalVariables().get( "STDERR" ) );
    }

    private static void parseHttpHeaders( HttpRequest request, RubyHash env )
    throws RackException
    {
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

    private static void doAccept( HttpRequest request, RubyHash env )
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
        if( request.containsHeader( HttpHeaders.Names.AUTHORIZATION ) )
        {
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
            throws RackException
    {
        if( request.containsHeader( HttpHeaders.Names.CONTENT_LENGTH ) )
        {
            String value = request.getHeader( HttpHeaders.Names.CONTENT_LENGTH );
            if( ! value.matches("^\\d+$") )
            {
                throw new RackException("CONTENT_LENGTH must consist of digits only.");
            }
            else
            {
                env.put("HTTP_CONTENT_LENGTH", value);
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
            env.put( "HTTP_CONTENT_TYPE", request.getHeader( HttpHeaders.Names.CONTENT_TYPE ) );
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
