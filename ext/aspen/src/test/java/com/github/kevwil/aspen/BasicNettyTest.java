package com.github.kevwil.aspen;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BasicNettyTest {
    private EmbeddedChannel channel;
    private ChannelPipeline pipeline;

    @Before
    public void setUp() {
        channel = new EmbeddedChannel();
        pipeline = channel.pipeline();
        setupHandlers();
    }

    @Test
    public void shouldWork() {
        assertTrue(channel.writeInbound("method_missing"));
        assertEquals(1, channel.inboundMessages().size());
        String result = channel.readInbound();
        assertEquals(0, channel.inboundMessages().size());
        System.out.println(result);
    }

    @Test
    public void fire() {
        pipeline.fireChannelRead("hello");
        String result = channel.readInbound();
        assertEquals("hello", result);
    }

    @Test
    public void myHandler() {
        pipeline.addLast(new DummyHandler());

        channel.writeInbound("hello", "world");
        channel.flush();
        while( ! channel.outboundMessages().isEmpty()) {
            ByteBuf msg = channel.readOutbound();
            System.out.println(msg.toString(Charset.defaultCharset()));
        }
    }

    private void setupHandlers() {
        pipeline.addLast("stringDecoder", new StringDecoder(StandardCharsets.UTF_8));
//        pipeline.addLast("stringEncoder", new StringEncoder(StandardCharsets.UTF_8));
    }

    private static class DummyHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {;
            ctx.writeAndFlush(Unpooled.copiedBuffer(msg.toUpperCase(), StandardCharsets.UTF_8));
            // Doesn't work right without StringEncoder in pipeline
//            ctx.writeAndFlush(msg.toUpperCase());
        }
    }
}
