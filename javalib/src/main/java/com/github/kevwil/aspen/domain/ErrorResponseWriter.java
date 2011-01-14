package com.github.kevwil.aspen.domain;

import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;

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
        HttpResponse httpResponse = createHttpResponse( response );
		httpResponse.setHeader( HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8" );
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
		httpResponse.setContent(
                ChannelBuffers.copiedBuffer( builder.toString(),
                                             Charset.forName( "UTF-8" ) ) );

		if( request.isKeepAlive() )
        {
            writeContentLength( httpResponse );
            writeToChannel( context, httpResponse, ChannelFutureListener.CLOSE_ON_FAILURE );
        }
		else
		{
			httpResponse.setHeader( HttpHeaders.Names.CONNECTION, "close" );
            writeToChannel( context, httpResponse, ChannelFutureListener.CLOSE );
		}
    }

    private void writeContentLength( final HttpResponse httpResponse )
    {
        httpResponse.setHeader( HttpHeaders.Names.CONTENT_LENGTH,
                                String.valueOf( httpResponse.getContent().readableBytes() ) );
    }

    private void writeToChannel( final ChannelHandlerContext context, final HttpResponse response, ChannelFutureListener future )
    {
        context.getChannel().write( response ).addListener( future );
    }
}
