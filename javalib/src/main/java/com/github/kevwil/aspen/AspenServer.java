package com.github.kevwil.aspen;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class AspenServer
{
    private String _host;
    private int _port;
    private Boolean _running;
    private ServerBootstrap _bootstrap;
    private ChannelGroup _allChannels;
    private ChannelFactory _channelFactory;

    /**
     * bootstrap the Netty channel factory
     * @param host hostname / ip address to bind to
     * @param port tcp socket port to listen on
     * @param rack callback proxy to pass data to/from Rack
     */
    public AspenServer( final String host, final int port, final RackProxy rack )
    {
        _running = false;
        _host = host;
        _port = port;
        _allChannels = new DefaultChannelGroup( "aspen-server" );
        _channelFactory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool() );
        _bootstrap = new ServerBootstrap( _channelFactory );
        _bootstrap.setOption( "child.tcpNoDelay", true );
        _bootstrap.setOption( "child.keepAlive", true );
        _bootstrap.setPipelineFactory( new RackHttpServerPipelineFactory( rack ) );
    }

    /**
     * begin listening on the given socket address
     */
    public void start()
    {
        try
        {
            Channel channel = _bootstrap.bind( new InetSocketAddress( _host, _port ) );
            _allChannels.add( channel );
            _running = true;
        }
        catch( Exception e )
        {
            System.err.println( "error starting Netty channel" );
            e.printStackTrace( System.err );
        }
    }

    /**
     * stop all channels
     */
    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    public void stop()
    {
        if( _running )
        {
            try
            {
                ChannelGroupFuture future = _allChannels.close();
                future.awaitUninterruptibly();
                _channelFactory.releaseExternalResources();
                _bootstrap.releaseExternalResources();
            }
            catch( Exception e )
            {
                System.err.println( "error stopping Netty channels" );
                e.printStackTrace( System.err );
            }
        }
        else
        {
            System.err.println( "stop called when server not running" );
            throw new RuntimeException( "cannot stop, not running" );
        }
    }

    /**
     * is the socket server running?
     * @return true or false
     */
    public boolean isRunning()
    {
        return _running;
    }
}
