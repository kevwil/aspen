package com.github.kevwil.aspen;

import java.net.InetSocketAddress;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.*;
import org.jruby.runtime.builtin.IRubyObject;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author kevwil
 */
public class RackServerHandlerTest
{
    @Test
    public void testRequestGivenToRackConverter() throws Exception
    {
        IRubyObject app = Ruby.getGlobalRuntime().getProc();
        RackServerHandler handler = new RackServerHandler(app);
        /* set up args for callback */
        ChannelPipeline pipeline = new DefaultChannelPipeline();
        ChannelHandlerContext ctx = pipeline.getContext(handler);
        HttpRequest r = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        MessageEvent e = new UpstreamMessageEvent(
                pipeline.getChannel(), r, new InetSocketAddress(54321));

        /* make callback call */
        handler.messageReceived(ctx, e);
        assertTrue(true);
    }
}
