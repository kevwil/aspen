package com.github.kevwil.aspen.domain;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.*;

import java.nio.charset.Charset;

/**
 * @author kevwil
 * @since Dec. 23, 2010
 */
public class DefaultResponseWriter
extends HttpResponseWriterBase
{
    @Override
    public void write( final ChannelHandlerContext context, final Request request, final Response response )
    {
        HttpResponse httpResponse = createHttpResponse( response );
        addHeaders( response, httpResponse );
        if( response.hasBody() )
        {
            StringBuilder sb = new StringBuilder( response.getBody().toString() ).append( "\r\n" );
            ChannelBuffer cb = ChannelBuffers.copiedBuffer( sb.toString(), Charset.forName( "UTF-8" ) );
            httpResponse.setContent( cb );
        }
        if( request.isKeepAlive() )
        {
            httpResponse.setHeader( HttpHeaders.Names.CONTENT_LENGTH, String.valueOf( httpResponse.getContent().readableBytes() ) );
            context.getChannel().write( httpResponse ).addListener( ChannelFutureListener.CLOSE_ON_FAILURE );
        }
        else
        {
            httpResponse.setHeader( HttpHeaders.Names.CONNECTION, "close" );
            context.getChannel().write( httpResponse ).addListener( ChannelFutureListener.CLOSE );
        }
    }

    private void addHeaders( final Response response, final HttpResponse httpResponse )
    {
        for( String name : response.getHeaderNames() )
        {
            for( String value : response.getHeaders( name ) )
            {
                httpResponse.addHeader( name, value );
            }
        }
    }
}
