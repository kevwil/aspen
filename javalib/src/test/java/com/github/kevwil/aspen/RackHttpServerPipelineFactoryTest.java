package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class RackHttpServerPipelineFactoryTest
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
        ChannelPipeline pipeline =
                new RackHttpServerPipelineFactory( rack ).getPipeline();
        assertNotNull( pipeline );
        ChannelHandler handler = pipeline.getLast();
        assertNotNull( handler );
        assertTrue( handler instanceof NettyServerHandler );
        verify(rack);
    }
}
