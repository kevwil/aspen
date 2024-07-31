package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertTrue;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class RackChannelInboundHandlerTest
{
    @Test
    public void shouldPassRequestToProxyAndReceiveResponse()
    {
        Ruby rubyRuntime = Ruby.getGlobalRuntime();
        // mocks
        RackProxy rack = createMock(RackProxy.class);
        Request request = createMockBuilder(Request.class)
                .withConstructor(
                        RackUtil.buildDummyChannelHandlerContext("localhost", "80"),
                        new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/"),
                        rubyRuntime
                ).createMock();

        // netty setup
        EmbeddedChannel channel = new EmbeddedChannel();

        // test object setup
        RackChannelInboundHandler handler = new RackChannelInboundHandler(rack);
        channel.pipeline().addLast("rack", handler);

        // test
        Response response = new Response(request);
        expect(rack.getRuntime()).andReturn(rubyRuntime).anyTimes();
        expect(rack.process(anyObject(Request.class))).andReturn(response);

        replay(request);
        replay(rack);

        assertTrue(channel.writeInbound(request.getHttpRequest()));

        verify(request);
        verify(rack);

        channel.finishAndReleaseAll();
    }
}
