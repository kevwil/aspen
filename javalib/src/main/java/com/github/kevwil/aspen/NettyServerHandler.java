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
@ChannelPipelineCoverage("one")
public class NettyServerHandler
extends SimpleChannelUpstreamHandler
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
        Object response = _rack.process( (HttpRequest)e.getMessage() );
        // write the response out
        e.getChannel().write( response ).addListener( ChannelFutureListener.CLOSE );
    }

    @SuppressWarnings({"ThrowableResultOfMethodCallIgnored"})
    @Override
    public void exceptionCaught( final ChannelHandlerContext ctx, final ExceptionEvent e )
    throws Exception
    {
        Channel ch = e.getChannel();
        Throwable cause = e.getCause();
        if( cause instanceof TooLongFrameException )
        {
            sendError( ctx, HttpResponseStatus.BAD_REQUEST );
            return;
        }
        cause.printStackTrace();
        if( ch.isConnected() )
        {
            sendError( ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR );
        }
    }

    private void sendError( ChannelHandlerContext ctx, HttpResponseStatus status )
    {
        HttpResponse response = new DefaultHttpResponse( HttpVersion.HTTP_1_1, status );
        response.setHeader(
            HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8" );
        response.setContent( ChannelBuffers.copiedBuffer(
            "Failure: " + status.toString() + "\r\n", "UTF-8") );
        // Close the connection as soon as the error message is sent.
        ctx.getChannel().write( response ).addListener( ChannelFutureListener.CLOSE );
    }
}
