package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.Test;

import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class RackChannelInboundHandlerTest
{
    @Test
    public void shouldPassRequestToProxyAndReceiveResponse() throws Exception
    {
        EventLoopGroup group = null;
        try {
            RackProxy rack = createMock(RackProxy.class);
            group = new NioEventLoopGroup();
            Channel channel = new ReflectiveChannelFactory<>(NioServerSocketChannel.class).newChannel();
            group.register(channel);
            ChannelPipeline pipeline = channel.pipeline();
            RackChannelInboundHandler handler = new RackChannelInboundHandler(rack);
            pipeline.addLast("handler", handler);
            FullHttpRequest hr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/");
            ChannelHandlerContext ctx = RackUtil.buildDummyChannelHandlerContext("localhost", "80");
            Request request = createMockBuilder(Request.class).withConstructor(ctx, hr, Ruby.getGlobalRuntime()).createMock();
            Response response = new Response(request);

            expect(rack.getRuntime()).andReturn(Ruby.getGlobalRuntime());
            expect(rack.process(anyObject(Request.class))).andReturn(response);
            replay(request);
            replay(rack);

            pipeline.fireChannelRead(hr);

            verify(request);
            verify(rack);
        }
        finally
        {
            if(group != null) {
                group.shutdownGracefully();
                group.terminationFuture().sync();
            }
        }
    }
}
