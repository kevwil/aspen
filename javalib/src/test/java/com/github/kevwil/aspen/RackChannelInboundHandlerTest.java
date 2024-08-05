package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.Request;
import com.github.kevwil.aspen.domain.Response;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class RackChannelInboundHandlerTest
{
    private RackProxy rack;
    private Channel channel;
    private ChannelPipeline pipeline;

    @Before
    public void setUp() throws Exception {
        rack = createMock(RackProxy.class);
        expect(rack.getRuntime()).andReturn(Ruby.getGlobalRuntime()).anyTimes();

        channel = new EmbeddedChannel(
                new RackChannelInboundHandler(rack));
        pipeline = channel.pipeline();
    }

    @After
    public void tearDown() throws Exception {
        verify(rack);
    }

    @Test
    public void shouldSayHello() {
        DefaultFullHttpRequest httpRequest = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/hello");
        Response response = new Response();
        response.setBody("hello");

        expect(rack.process(anyObject(Request.class))).andReturn(response).anyTimes();
        replay(rack);

        assertTrue(channel.isActive());
        assertTrue(channel.isOpen());
        assertTrue(channel.isRegistered());
        assertTrue(channel.isWritable());

        try {
            pipeline.fireChannelRead(httpRequest);
        } catch (Throwable t) {
            t.printStackTrace(System.err);
            fail(t.getLocalizedMessage());
        }

        assertEquals(HttpResponseStatus.OK, response.getResponseStatus());
        assertTrue(response.hasBody());
        assertFalse(response.hasException());
        System.out.println(response.getBody());
    }

//    @Test
//    public void shouldWork(){
//        Request request = new Request(channel.pipeline().context("rack"),
//                new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/"),
//                Ruby.getGlobalRuntime());
//        Response r = new Response();
//        expect(rack.process(anyObject(Request.class))).andReturn(r);
//        expect(rack.getRuntime()).andReturn(Ruby.getGlobalRuntime());
//
//        replay(rack);
//        boolean written = channel.writeInbound(request.getHttpRequest());
////        assertTrue(written);
//        Object inboundChannelResponse = channel.readInbound();
//        Object outbound = channel.readOutbound();
////        assertEquals(1, channel.outboundMessages().size());
//        channel.finishAndReleaseAll();
//        Response response = (Response) outbound;
//
//        assertTrue(inboundChannelResponse instanceof Response);
//        assertEquals(HttpResponseStatus.OK, response.getResponseStatus());
//        assertTrue(response.hasBody());
//        assertFalse(response.hasException());
//    }

//    @Test
//    public void shouldPassRequestToProxyAndReceiveResponse()
//    {
//        Ruby rubyRuntime = Ruby.getGlobalRuntime();
//        // mocks
//        RackProxy rack = createMock(RackProxy.class);
//        Request request = createMockBuilder(Request.class)
//                .withConstructor(
//                        RackUtil.buildDummyChannelHandlerContext("localhost", "80"),
//                        new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/"),
//                        rubyRuntime
//                ).createMock();
//
//        // netty setup
//        EmbeddedChannel channel = new EmbeddedChannel();
//
//        // test object setup
//        RackChannelInboundHandler handler = new RackChannelInboundHandler(rack);
//        channel.pipeline().addLast("rack", handler);
//
//        // test
//        Response response = new Response();
//        expect(rack.getRuntime()).andReturn(rubyRuntime).anyTimes();
//        expect(rack.process(anyObject(Request.class))).andReturn(response);
//
//        replay(request);
//        replay(rack);
//
//        assertTrue(channel.writeInbound(request.getHttpRequest()));
//
//        verify(request);
//        verify(rack);
//
//        channel.finishAndReleaseAll();
//    }
}
