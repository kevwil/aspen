package com.github.kevwil.aspen;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class RackHttpServerPipelineFactoryTest
{
    private IRubyObject nil;

    @Before
    public void setUp()
    throws Exception
    {
        nil = Ruby.getGlobalRuntime().getNil();
    }

    @Test
    public void testPipelineHasRackHandlerLast()
    throws Exception
    {
        ChannelPipeline pipeline =
                new RackHttpServerPipelineFactory( nil ).getPipeline();
        assertNotNull( pipeline );
        ChannelHandler handler = pipeline.getLast();
        assertNotNull( handler );
        assertTrue( handler instanceof RackServerHandler );
    }
}
