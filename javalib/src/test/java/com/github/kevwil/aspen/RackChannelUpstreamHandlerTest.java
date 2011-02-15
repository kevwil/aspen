package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.Test;

import java.net.InetSocketAddress;

import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class RackChannelUpstreamHandlerTest
{
    @Test
    public void shouldPassRequestToProxyAndReceiveResponse()
    {
        RackProxy rack = createMock( RackProxy.class );
        RackChannelUpstreamHandler handler = new RackChannelUpstreamHandler( rack );
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "handler", handler );
        Channel channel = new DefaultLocalServerChannelFactory().newChannel( pipeline );
        HttpRequest hr = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        MessageEvent e = new UpstreamMessageEvent(
                channel, hr, new InetSocketAddress( 54321 ) );
        ChannelHandlerContext ctx = RackUtil.buildDummyChannelHandlerContext( "localhost", "80" );
        Request request = createMockBuilder( Request.class ).withConstructor( ctx, hr, Ruby.getGlobalRuntime() ).createMock();
        Response response = new Response( request );

        expect( rack.getRuntime() ).andReturn( Ruby.getGlobalRuntime() );
        expect( rack.process( anyObject( Request.class ) ) ).andReturn( response );
        replay( request );
        replay( rack );

        pipeline.sendUpstream( e );

        verify( request );
        verify( rack );
    }
}
