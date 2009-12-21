package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.buffer.ChannelBuffers;

/**
 * Netty callback handler base class with common exception handling
 *
 * @author kevwil
 * @since Dec. 21, 2009
 */
public class AspenUpstreamHandlerBase
extends SimpleChannelUpstreamHandler
{
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
