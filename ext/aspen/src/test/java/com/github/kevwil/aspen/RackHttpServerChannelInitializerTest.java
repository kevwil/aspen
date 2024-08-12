package com.github.kevwil.aspen;

import io.netty.channel.*;
import static org.junit.Assert.*;

import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.junit.*;

import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class RackHttpServerChannelInitializerTest
{
    private RackProxy rack;

    @Before
    public void setUp()
    throws Exception
    {
        rack = createMock( RackProxy.class );
    }

    @Test
    public void testPipelineHasRackHandlerLast()
    throws Exception
    {
        replay(rack);
        RackHttpServerChannelInitializer channelInitializer = new RackHttpServerChannelInitializer(rack);
        Channel channel = new NioServerSocketChannel();
        channelInitializer.initChannel(channel);
        ChannelPipeline pipeline =
                channel.pipeline();
        assertNotNull( pipeline);
        ChannelHandler handler = pipeline.last();
        assertNotNull( handler );
        assertTrue( handler instanceof RackChannelInboundHandler);
        verify(rack);
//        channel.closeFuture().sync();
    }
}
