package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;

/**
 * This is the Netty callback handler,
 * used by the socket listener.
 * @author kevwil
 * @since Jun 25, 2009
 */
public class NettyServerHandler
extends MultipleInstanceHandler
{
    private RackProxy _rack;

    public NettyServerHandler( final RackProxy rackProxy )
    {
        _rack = rackProxy;
    }

    @Override
    public void messageReceived( final ChannelHandlerContext ctx, final MessageEvent e )
    throws Exception
    {
        // send the request to Rack and receive the response
        Object response = _rack.process( ctx, (HttpRequest)e.getMessage() );
        // write the response out
        e.getChannel().write( response ).addListener( ChannelFutureListener.CLOSE );
    }
}
