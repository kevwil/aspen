package com.github.kevwil.aspen.domain;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.*;

import java.nio.charset.StandardCharsets;

import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jan 04, 2011
 */
public class DefaultResponseWriterTest
{
    private MockDefaultResponseWriter writer;
    private FullHttpResponse httpResponse;
    private FullHttpRequest httpRequest;
    private Request request;
    private Response response;
    private ChannelHandlerContext context;
    private Channel channel;
    private ChannelPipeline pipeline;
    private ChannelFuture future;

    @Before
    public void setUp()
    {
        writer = new MockDefaultResponseWriter();
        httpResponse = createMock( FullHttpResponse.class );
        channel = createMock(Channel.class);
        pipeline = createMock(ChannelPipeline.class);
        context = createMock(ChannelHandlerContext.class);
        future = createMock(ChannelFuture.class);

        expect(context.channel()).andReturn(channel);
        expect(channel.write(anyObject(FullHttpResponse.class))).andReturn(future);
        expect(future.addListener(anyObject())).andReturn(future);
        expect(httpResponse.touch(anyObject())).andReturn(httpResponse).anyTimes();

        writer.setResponse(httpResponse);
        httpRequest = new DefaultFullHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );

        request = new Request(context, httpRequest, Ruby.getGlobalRuntime() );
        response = new Response(request);
    }

    @After
    public void tearDown() {
        verify(httpResponse);
        verify(channel);
        verify(pipeline);
        verify(context);
        verify(future);
    }

    private void replayAll()
    {
        replay(httpResponse);
        replay(channel);
        replay(pipeline);
        replay(context);
        replay(future);
    }

    @Test
    public void shouldSetContentIfBodyPresent()
    {
        HttpUtil.setKeepAlive(httpRequest, false );
        expect(httpResponse.replace(anyObject(ByteBuf.class))).andReturn(httpResponse);
        expectNonKeepAlive(httpResponse);
        replayAll();
        response.setBody( "hello" );
        writer.write(context, request, response);
    }

    @Test
    public void shouldNotSetContentIfNoBodyPresent()
    {
        HttpUtil.setKeepAlive(httpRequest, false );
        expectNonKeepAlive(httpResponse);
        replayAll();
        writer.write(context, request, response);
    }

    @Test
    public void shouldSetContentLengthIfKeepAlive()
    {
        expect(httpResponse.replace(anyObject(ByteBuf.class))).andReturn(httpResponse);
        expectKeepAlive( Unpooled.copiedBuffer( "hello\r\n",
                                                      StandardCharsets.UTF_8 ),
                httpResponse);
        replayAll();
        response.setBody( "hello" );
        writer.write(context, request, response);
    }

    @Test
    public void shouldSetConnectionHeaderIfNotKeepAlive()
    {
        HttpUtil.setKeepAlive(httpRequest, false );
        expectNonKeepAlive(httpResponse);
        replayAll();
        writer.write(context, request, response);
    }

    private void expectNonKeepAlive( FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(HttpHeaderNames.CONNECTION, "close");
        expect(httpResponse.headers()).andReturn(headers);
    }

    private void expectKeepAlive(ByteBuf content, FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set(HttpHeaderNames.CONTENT_LENGTH, content.readableBytes());
        expect( httpResponse.content() ).andReturn( content );
        expect(httpResponse.headers()).andReturn(headers);
    }

    private static class MockDefaultResponseWriter
    extends DefaultResponseWriter
    {
        private FullHttpResponse _response;

        public void setResponse( final FullHttpResponse response )
        {
            _response = response;
        }

        @Override
        public FullHttpResponse createHttpResponse( final Response response )
        {
            return _response;
        }
    }
}
