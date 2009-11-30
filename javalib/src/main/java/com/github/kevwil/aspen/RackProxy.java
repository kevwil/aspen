package com.github.kevwil.aspen;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author kevwil
 * @since Nov 25, 2009
 */
public interface RackProxy
{
    /**
     * pass the Netty messageReceived call up to Ruby
     *
     * ... you can write a 'ChunkedInput' so that the 'ChunkedWriteHandler'
     * can pick it up and fetch the content of the stream chunk by chunk
     * and write the fetched chunk downstream:
     * Channel ch = ...;
     * ch.write(new ChunkedFile(new File("video.mkv"));
     * @param cxt the context for the handler
     * @param r the HttpRequest
     * @return the HttpResponse, or ChunkedInput
     */
    Object process( ChannelHandlerContext cxt, HttpRequest r );
}
