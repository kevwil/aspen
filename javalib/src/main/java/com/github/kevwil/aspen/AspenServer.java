package com.github.kevwil.aspen;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.group.*;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jruby.runtime.builtin.IRubyObject;

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

    public AspenServer( final String host, final int port, final IRubyObject app )
    {
        _host = host;
        _port = port;
        _allChannels = new DefaultChannelGroup( "aspen-server" );
        _channelFactory = new NioServerSocketChannelFactory(
                Executors.newCachedThreadPool(),
                Executors.newCachedThreadPool() );
        _bootstrap = new ServerBootstrap( _channelFactory );
        _bootstrap.setOption("child.tcpNoDelay", true);
        _bootstrap.setOption("child.keepAlive", true);
        _bootstrap.setPipelineFactory( new RackHttpServerPipelineFactory( app ) );
    }

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
            e.printStackTrace( System.err );
        }
    }

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
                e.printStackTrace( System.err );
            }
        }
        else
        {
            throw new RuntimeException( "cannot stop, not running" );
        }
    }
}
