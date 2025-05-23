package com.github.kevwil.aspen;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static java.lang.Thread.sleep;
import static org.junit.Assert.*;

public class BasicHttpTest {
    private EmbeddedChannel channel;
    private ChannelPipeline pipeline;

    @Before
    public void setUp() {
        channel = new EmbeddedChannel();
        pipeline = channel.pipeline();
        setupHandlers();
    }

    @Test
    public void hello() throws InterruptedException {
        requestUriReturnedAsContent("hello");
        requestUriReturnedAsContent("foo");
    }

    private void requestUriReturnedAsContent(String word) throws InterruptedException {
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/"+word);
        channel.writeInbound(request);
        channel.flush();
        sleep(3);
        assertFalse(channel.outboundMessages().isEmpty());
        FullHttpResponse response = channel.readOutbound();
        assertNotNull(response);
        assertEquals(HttpResponseStatus.OK, response.status());
        ByteBuf content = response.content();
        String output = content.toString(Charset.defaultCharset());
        System.out.println(output);
        assertEquals(word, output);
    }

    private void setupHandlers() {
        pipeline.addLast("timeout", new ReadTimeoutHandler(30));
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("inflator", new HttpContentDecompressor(256));
        pipeline.addLast("URLDecoder", new RequestURLDecoder());

        pipeline.addLast(new EchoHandler());

        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("chunkWriter", new ChunkedWriteHandler());
        pipeline.addLast("deflator", new HttpContentCompressor());
        pipeline.addLast("aggregator", new HttpObjectAggregator(20480));
    }

    private static class EchoHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
            DefaultFullHttpResponse response = new DefaultFullHttpResponse(
                    request.protocolVersion(),
                    HttpResponseStatus.OK,
                    Unpooled.copiedBuffer(request.uri().substring(1), Charset.defaultCharset())
            );
            ctx.writeAndFlush(response);
        }
    }
}
