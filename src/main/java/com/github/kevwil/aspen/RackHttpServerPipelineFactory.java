package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author kevinw
 * @since Jun 25, 2009
 */
public class RackHttpServerPipelineFactory
implements ChannelPipelineFactory
{
    private AspenServer _server;

    public RackHttpServerPipelineFactory( final AspenServer server )
    {
        _server = server;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception
    {
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "decoder", new HttpRequestDecoder() );
        pipeline.addLast( "encoder", new HttpResponseEncoder() );
        pipeline.addLast( "chunkedWriter", new ChunkedWriteHandler() );
        pipeline.addLast( "handler", new RackServerHandler( _server ) );
        return pipeline;
    }
}
