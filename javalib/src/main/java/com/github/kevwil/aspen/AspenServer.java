package com.github.kevwil.aspen;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.ChannelGroupFuture;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;


/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class AspenServer
{
    private static final ChannelGroup ALL_CHANNELS = new DefaultChannelGroup("Aspen", GlobalEventExecutor.INSTANCE);
    private Boolean running;
    private final ServerBootstrap bootstrap;
    private final ServerBootstrapFactory bootstrapFactory = new ServerBootstrapFactory();

    /**
     * bootstrap the Netty channel factory
     * @param host hostname / ip address to bind to
     * @param port tcp socket port to listen on
     * @param rack callback proxy to pass data to/from Rack
     */
    public AspenServer( final String host, final int port, final RackProxy rack )
    {
        running = false;
        bootstrap = bootstrapFactory.newServerBootstrap(0);
        bootstrap.localAddress(host, port)
                  .option(ChannelOption.SO_BACKLOG, 1024)
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
            ChannelFuture boundFuture = bootstrap.bind();
            running = true;
            boundFuture.addListener(
                    (GenericFutureListener<ChannelFuture>) future ->
                            ALL_CHANNELS.add(future.channel()));
            boundFuture.awaitUninterruptibly();
        }
        catch( Exception e )
        {
            System.err.println( "error starting Netty channel" );
            e.printStackTrace( System.err );
        }
    }

    public void awaitShutdown() {
        Runtime.getRuntime().addShutdownHook(new AspenServerShutdownHook(this));
        boolean interrupted = false;
        do {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                interrupted = true;
            }
        } while (!interrupted);
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
                running = false;
                ChannelGroupFuture allFuture = ALL_CHANNELS.close();
                bootstrapFactory.shutdownGracefully(false);
                allFuture.awaitUninterruptibly();
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
