package com.github.kevwil.aspen;

import static org.easymock.EasyMock.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.junit.*;

import java.net.InetSocketAddress;

/**
 * @author kevwil
 * @since Jul 1, 2009
 */
public class NettyServerHandlerTest
{
    @Test
    public void shouldPassRequestToProxyAndGetResponse() throws Exception
    {
        RackProxy rack = createMock(RackProxy.class);
        NettyServerHandler handler = new NettyServerHandler( rack );
        
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "rack", handler );
        Channel channel = new DefaultLocalServerChannelFactory().newChannel( pipeline );
        HttpRequest r = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        MessageEvent e = new UpstreamMessageEvent(
                channel, r, new InetSocketAddress( 54321 ) );

        expect(rack.process( eq(r) ) ).andReturn( "boo!" );
        replay(rack);
        
        pipeline.sendUpstream( e );

        verify(rack);
    }
}
