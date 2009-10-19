package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 *
 * @author kevwil
 */
public class RackServerHandlerTest
{
    @Test
    public void testRequestGivenToRackConverter() throws Exception
    {
        IRubyObject app = createProc(200, "Hello World");
        RackServerHandler handler = new RackServerHandler(app);
        /* set up args for callback */
        ChannelPipeline pipeline = new DefaultChannelPipeline();
        pipeline.addLast( "rack", handler );
        ChannelHandlerContext ctx = pipeline.getContext(handler);
        HttpRequest r = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/");
        MessageEvent e = new UpstreamMessageEvent(
                ctx.getChannel(), r, new InetSocketAddress(54321));

        /* make callback call */
        handler.messageReceived(ctx, e);
        assertTrue(true);
    }

    private IRubyObject createProc( final int status, final String body )
    {
        Ruby runtime = Ruby.getGlobalRuntime();
        return runtime.evalScriptlet( "def call(env); [" + status + ", {}, \"" + body + "\"]; end" );
    }
}
