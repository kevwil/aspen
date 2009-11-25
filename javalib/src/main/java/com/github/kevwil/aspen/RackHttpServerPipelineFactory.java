package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Creates the pipeline of Netty middleware and RackServerHandler
 * @author kevwil
 * @since Jun 25, 2009
 */
public class RackHttpServerPipelineFactory
implements ChannelPipelineFactory
{
    private IRubyObject _app;

    public RackHttpServerPipelineFactory( final IRubyObject app )
    {
        _app = app;
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
        pipeline.addLast( "encoder", new HttpResponseEncoder() );
        pipeline.addLast( "chunkedWriter", new ChunkedWriteHandler() );
        pipeline.addLast( "handler", new RackServerHandler( _app ) );
        return pipeline;
    }
}
