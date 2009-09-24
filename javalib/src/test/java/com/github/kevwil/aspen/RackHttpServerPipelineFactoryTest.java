package com.github.kevwil.aspen;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author kevwil
 */
public class RackHttpServerPipelineFactoryTest
{
    @Test
    public void testPipelineHasRackHandlerLast()
    throws Exception
    {
        ChannelPipeline pipeline =
                new RackHttpServerPipelineFactory().getPipeline();
        assertNotNull(pipeline);
        ChannelHandler handler = pipeline.getLast();
        assertNotNull(handler);
        assertTrue(handler instanceof RackServerHandler);
    }
}
