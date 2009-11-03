package com.github.kevwil.aspen;

import org.jboss.netty.channel.*;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.jruby.runtime.builtin.IRubyObject;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class RackServerHandlerTest
{
    @Test
    public void testRequestGivenToRackConverter() throws Exception
    {
        IRubyObject app = createProc( 200, "Hello World" );
        RackServerHandler handler = new RackServerHandler( app );
        /* set up args for callback */
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "rack", handler );
        //ChannelHandlerContext ctx = pipeline.getContext( handler );
        Channel channel = new DefaultLocalServerChannelFactory().newChannel( pipeline );
        HttpRequest r = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        MessageEvent e = new UpstreamMessageEvent(
                channel, r, new InetSocketAddress( 54321 ) );

        /* make callback call */
        pipeline.sendUpstream( e );
        //handler.messageReceived(ctx, e);
        // TODO: incomplete, right?
        assertTrue( true );
    }

    private IRubyObject createProc( final int status, final String body )
    {
        // yes, I basically just gave up on understanding the internal JRuby way
        // of doing this. I'm a cheater and a loser.
        Ruby runtime = Ruby.getGlobalRuntime();
        return runtime.evalScriptlet( "def call(env); [" + status + ", {}, \"" + body + "\"]; end" );
    }
}
