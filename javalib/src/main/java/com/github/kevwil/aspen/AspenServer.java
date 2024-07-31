package com.github.kevwil.aspen;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;


/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class AspenServer
{
    private Boolean running;
    private final ServerBootstrap bootstrap;
    private final EventLoopGroup bossGroup;
    private final EventLoopGroup workerGroup;

    /**
     * bootstrap the Netty channel factory
     * @param host hostname / ip address to bind to
     * @param port tcp socket port to listen on
     * @param rack callback proxy to pass data to/from Rack
     */
    public AspenServer( final String host, final int port, final RackProxy rack )
    {
        running = false;
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                  .channel(NioServerSocketChannel.class)
                  .localAddress(host, port)
                  .option(ChannelOption.SO_BACKLOG, 100)
                  .childOption(ChannelOption.TCP_NODELAY, true)
                  .childOption(ChannelOption.SO_KEEPALIVE, true)
                  .childHandler(new RackHttpServerChannelInitializer(rack));
    }

    /**
     * begin listening on the given socket address
     */
    public void start()
    {
        if( isRunning() )
        {
            System.err.println( "Unable to start - already running" );
            return;
        }
        try
        {
            ChannelFuture future = bootstrap.bind().sync();
            running = true;
            future.channel().closeFuture().sync();
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
        if( isRunning() )
        {
            try
            {
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
                bossGroup.terminationFuture().sync();
                workerGroup.terminationFuture().sync();
                running = false;
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
        return running;
    }
}
