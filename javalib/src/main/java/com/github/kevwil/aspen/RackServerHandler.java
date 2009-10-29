package com.github.kevwil.aspen;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author kevwil
 * @since Jun 25, 2009
 */
@ChannelPipelineCoverage("one")
public class RackServerHandler
extends SimpleChannelUpstreamHandler
{
    private IRubyObject _app;

    public RackServerHandler( final IRubyObject app )
    {
        _app = app;
    }

    @Override
    public void messageReceived( final ChannelHandlerContext ctx, final MessageEvent e )
    throws Exception
    {
        Ruby runtime = _app.getRuntime();
        HttpRequest request = (HttpRequest)e.getMessage();
        IRubyObject env = RackEnvironmentMaker.build( request, runtime );
        RubyArray rackOutput =
                _app.callMethod( runtime.getCurrentContext(), "call", env )
                .convertToArray();
        HttpResponse response = RackResponseTranslator.translate( rackOutput );
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
