package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import static org.junit.Assert.*;
import org.junit.*;

import java.net.*;

/**
 * @author kevwil
 * @since Nov 13, 2009
 */
public class RackEnvironmentMakerUriTest
{
    private static Ruby runtime;

    @BeforeClass
    public static void classInit()
    {
        runtime = Ruby.getGlobalRuntime();
    }

    @Test
    public void shouldIncludeHostAndPortInUrl() throws Exception
    {
        doTestGetProcessing( "http", "localhost", "12345", "/" );
        doTestGetProcessing( "http", "localhost", "80", "/" );
        doTestGetProcessing( "http", "localhost", null, "/" );
        doTestGetProcessing( "http", "localhost", "12345", "/foo/bar/baz" );
        doTestGetProcessing( "http", "localhost", null, "/foo/bar/baz" );
        doTestGetProcessing( "https", "localhost", "12345", "/" );
    }

    @Test
    public void shouldParseHostAndPortFromHeader() throws Exception
    {
        doTestHostHeaderProcessing( "http", "localhost", "12345", "/" );
        doTestHostHeaderProcessing( "http", "localhost", null, "/" );
    }

    @Test
    public void shouldIncludeQueryStringIfProvided() throws Exception
    {
        doTestGetProcessing( "http", "localhost", "80", "/search.pl?fname=kevin&lname=williams" );
    }

    @Test
    public void shouldFulfilMinimumQueryStringRequirementsIfNotProvided() throws Exception
    {
        
    }

    private void doTestHostHeaderProcessing( String protocol, String server, String port, String path )
    throws Exception
    {
        HttpRequest req = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, path );
        String header = (port != null && port.length() > 0) ? server+":"+port : server;
        req.addHeader( HttpHeaders.Names.HOST, header );
        ChannelHandlerContext ctx = buildChannelHandlerContext( server, port );
        RubyHash env = (RubyHash) RackEnvironmentMaker.build( ctx, req, runtime );

        assertEquals( protocol, env.get( "rack.url_scheme" ) );
        assertEquals( server, env.get( "SERVER_NAME" ) );
        if( port!= null && port.length() > 0 )
        {
            String ep = env.get( "SERVER_PORT" ).toString();
            assertEquals( port, ep );
        }
        else
        {
            String ep = env.get( "SERVER_PORT" ).toString();
            assertEquals( "80", ep );
        }
        String ep2 = (String) env.get( "PATH_INFO" );
        assertEquals( path, ep2 );
    }


    private void doTestGetProcessing( String protocol, String server, String port, String path )
    throws Exception
    {
        StringBuilder sb = new StringBuilder();
        sb.append( protocol );
        sb.append( "://" );
        sb.append( server );
        if( port!= null && port.length() > 0 && ! port.equals( "80" ) )
        {
            sb.append( ":" ).append( port );
        }
        sb.append( path );

        HttpRequest req = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, sb.toString() );
        ChannelHandlerContext ctx = buildChannelHandlerContext( server, port );
        RubyHash env = (RubyHash) RackEnvironmentMaker.build( ctx, req, runtime );

        assertEquals( protocol, env.get( "rack.url_scheme" ) );
        assertEquals( server, env.get( "SERVER_NAME" ) );
        if( port!= null && port.length() > 0 )
        {
            String ep = env.get( "SERVER_PORT" ).toString();
            assertEquals( port, ep );
        }
        else
        {
            String ep = env.get( "SERVER_PORT" ).toString();
            assertEquals( "80", ep );
        }
        String ep2 = (String) env.get( "PATH_INFO" );
        if( path.contains( "?" ) )
        {
            String[] pathAndQuery = path.split( "\\?" );
            assertEquals( pathAndQuery[0], ep2 );
            String query = env.get( "QUERY_STRING" ).toString();
            assertEquals( pathAndQuery[1], query );
        }
        else
        {
            assertEquals( path, ep2 );
        }
    }

    private ChannelHandlerContext buildChannelHandlerContext( final String server, final String port )
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

    private class DumDumCtx implements ChannelHandlerContext
    {
        private Channel channel;
        private DumDumCtx( InetSocketAddress address )
        {
            channel = new DumDumChannel( address );
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

    private class DumDumChannel implements Channel
    {
        private SocketAddress address;

        private DumDumChannel( final SocketAddress address )
        {
            this.address = address;
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
            return address;
        }

        public SocketAddress getRemoteAddress()
        {
            return null;
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
}
