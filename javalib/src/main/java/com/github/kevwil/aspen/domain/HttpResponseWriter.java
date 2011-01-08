package com.github.kevwil.aspen.domain;

import org.jboss.netty.channel.ChannelHandlerContext;

/**
 * @author kevwil
 * @since Dec. 23, 2010
 */
public interface HttpResponseWriter
{
    void write( ChannelHandlerContext context, Request request, Response response );
}
