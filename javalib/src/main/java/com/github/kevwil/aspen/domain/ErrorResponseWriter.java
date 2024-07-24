package com.github.kevwil.aspen.domain;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author kevwil
 * @since Dec. 23, 2010
 */
public class ErrorResponseWriter
extends HttpResponseWriterBase
{
    @SuppressWarnings( { "ThrowableResultOfMethodCallIgnored" } )
    @Override
    public void write( final ChannelHandlerContext context, final Request request, final Response response )
    {
        FullHttpResponse httpResponse = createHttpResponse( response );
		httpResponse.headers().set( HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8" );
        StringWriter builder =new StringWriter();
        response.getException().printStackTrace( new PrintWriter( builder ) );
//		StringBuilder builder = new StringBuilder( "Failure: " );
//		builder.append( response.getException().getLocalizedMessage() );
//		builder.append( "\r\n" );
//        for( StackTraceElement ste : response.getException().getStackTrace() )
//        {
//            builder.append( ste.toString() );
//		    builder.append( "\r\n" );
//        }
		httpResponse.replace(
                Unpooled.copiedBuffer( builder.toString(),
                                             StandardCharsets.UTF_8 ) );

		if( request.isKeepAlive() )
        {
            writeContentLength( httpResponse );
            writeToChannel( context, httpResponse, ChannelFutureListener.CLOSE_ON_FAILURE );
        }
		else
		{
			httpResponse.headers().set( HttpHeaderNames.CONNECTION, "close" );
            writeToChannel( context, httpResponse, ChannelFutureListener.CLOSE );
		}
    }

    private void writeContentLength( final FullHttpResponse httpResponse )
    {
        httpResponse.headers().set( HttpHeaderNames.CONTENT_LENGTH,
                                String.valueOf( httpResponse.content().readableBytes() ) );
    }

    private void writeToChannel( final ChannelHandlerContext context, final HttpResponse response, ChannelFutureListener future )
    {
        context.channel().write( response ).addListener( future );
    }
}
