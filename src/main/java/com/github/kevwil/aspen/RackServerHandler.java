package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;

/**
 * @author kevinw
 * @since Jun 25, 2009
 */
@ChannelPipelineCoverage("one")
public class RackServerHandler
extends SimpleChannelUpstreamHandler
{
//    private AspenServer _aspen;
//
//    public RackServerHandler( final AspenServer aspenServer )
//    {
//        _aspen = aspenServer;
//    }

    @Override
    public void messageReceived( final ChannelHandlerContext ctx, final MessageEvent e )
    throws Exception
    {
        // TODO if _aspen.isVerbose() then do logging
        // TODO pass to _aspen.getRackAdapter()
        super.messageReceived( ctx, e );
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
            sendError(ctx, HttpResponseStatus.BAD_REQUEST);
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
