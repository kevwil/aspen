package com.github.kevwil.aspen;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * Creates the pipeline of Netty middleware and RackServerHandler
 * @author kevwil
 * @since Jun 25, 2009
 */
public class RackHttpServerChannelInitializer
extends ChannelInitializer<Channel>
{
    private final RackProxy rack;
    private final boolean compressionEnabled;
    // private final int _maxChunkSize;

    public RackHttpServerChannelInitializer( final RackProxy rack )
    {
        this.rack = rack;
        compressionEnabled = false;
        // _maxChunkSize = 8*1024;
    }

    // ... you can write a 'ChunkedInput' so that the 'ChunkedWriteHandler'
    // can pick it up and fetch the content of the stream chunk by chunk
    // and write the fetched chunk downstream:
    // Channel ch = ...;
    // ch.write(new ChunkedFile(new File("video.mkv"));
    @Override
    protected void initChannel(Channel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder())
                .addLast("encoder", new HttpResponseEncoder())
                .addLast("chuckedWriter", new ChunkedWriteHandler());
        if(compressionEnabled)
        {
            pipeline.addLast("deflator", new HttpContentCompressor())
                    .addLast("inflator", new HttpContentDecompressor());
        }
        pipeline.addLast(new RackChannelInboundHandler(rack));
    }
}
