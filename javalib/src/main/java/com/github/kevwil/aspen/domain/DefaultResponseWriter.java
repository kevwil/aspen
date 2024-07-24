package com.github.kevwil.aspen.domain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.nio.charset.StandardCharsets;

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
        FullHttpResponse httpResponse = createHttpResponse( response );
        addHeaders( response, httpResponse );
        if( response.hasBody() )
        {
            ByteBuf buf = Unpooled.copiedBuffer(response.getBody().toString() + "\r\n", StandardCharsets.UTF_8);
            httpResponse.replace(buf);
        }
        if( request.isKeepAlive() )
        {
            httpResponse.headers().set( HttpHeaderNames.CONTENT_LENGTH, String.valueOf( httpResponse.content().readableBytes() ) );
            context.channel().write( httpResponse ).addListener( ChannelFutureListener.CLOSE_ON_FAILURE );
        }
        else
        {
            httpResponse.headers().set( HttpHeaderNames.CONNECTION, "close" );
            context.channel().write( httpResponse ).addListener( ChannelFutureListener.CLOSE );
        }
    }

    private void addHeaders( final Response response, final HttpResponse httpResponse )
    {
        for( String name : response.getHeaderNames() )
        {
            for( String value : response.getHeaders( name ) )
            {
                httpResponse.headers().add( name, value );
            }
        }
    }
}
