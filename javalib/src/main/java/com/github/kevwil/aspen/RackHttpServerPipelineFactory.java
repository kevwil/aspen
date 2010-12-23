package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;

/**
 * Creates the pipeline of Netty middleware and RackServerHandler
 * @author kevwil
 * @since Jun 25, 2009
 */
public class RackHttpServerPipelineFactory
implements ChannelPipelineFactory
{
    private final RackProxy _rack;
    private final boolean _compressionEnabled;
    private final int _maxChunkSize;

    public RackHttpServerPipelineFactory( final RackProxy rack )
    {
        _rack = rack;
        _compressionEnabled = false;
        _maxChunkSize = 8*1024;
    }

    // ... you can write a 'ChunkedInput' so that the 'ChunkedWriteHandler'
    // can pick it up and fetch the content of the stream chunk by chunk
    // and write the fetched chunk downstream:
    // Channel ch = ...;
    // ch.write(new ChunkedFile(new File("video.mkv"));
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "decoder", new HttpRequestDecoder() );
        pipeline.addLast( "aggregator", new HttpChunkAggregator( _maxChunkSize ) );
        pipeline.addLast( "encoder", new HttpResponseEncoder() );
//        pipeline.addLast( "chunkedWriter", new ChunkedWriteHandler() );
        if( _compressionEnabled )
        {
            pipeline.addLast( "deflator", new HttpContentCompressor() );
            pipeline.addLast( "inflator", new HttpContentDecompressor() );
        }
        pipeline.addLast( "handler", new NettyServerHandler( _rack ) );
        return pipeline;
    }
}
