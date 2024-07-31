package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.exception.ServiceException;
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
 * @since Jan 05, 2011
 */
public class ErrorResponseWriterTest
{
    private MockErrorResponseWriter writer;
    private FullHttpResponse httpResponse;
    private FullHttpRequest httpRequest;
    private Request request;
    private Response response;
    private ChannelHandlerContext context;
    private Throwable exception;
    private Channel channel;
    private ChannelPipeline pipeline;
    private ChannelFuture future;

    @Before
    public void setUp()
    {
        writer = new MockErrorResponseWriter();
        channel = createMock(Channel.class);
        pipeline = createMock(ChannelPipeline.class);
        context = createMock(ChannelHandlerContext.class);
        future = createMock(ChannelFuture.class);
        httpResponse = createMock( FullHttpResponse.class );
        writer.setResponse(httpResponse);
        httpRequest = new DefaultFullHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        exception = new ServiceException( "Oops!" );
        request = new Request(context, httpRequest, Ruby.getGlobalRuntime() );
        response = new Response(request);
        response.setException(exception);

        expect(context.channel()).andReturn(channel);
        expect(channel.write(anyObject(FullHttpResponse.class))).andReturn(future);
        expect(future.addListener(anyObject())).andReturn(future);
        expect(httpResponse.touch(anyObject())).andReturn(httpResponse).anyTimes();
    }

    @After
    public void tearDown()
    {
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
    public void shouldSetContentLengthIfKeepAlive()
    {
        expectContentType(httpResponse);
        expectSetContent(httpResponse, exception);
        expectKeepAlive( Unpooled.copiedBuffer( "hello\r\n", StandardCharsets.UTF_8 ),
                httpResponse);
        replayAll();
        writer.write(context, request, response);
    }

    @Test
    public void shouldSetConnectionHeaderIfNotKeepAlive()
    {
        HttpUtil.setKeepAlive(httpRequest, false );
        expectContentType(httpResponse);
        expectSetContent(httpResponse, exception);
        expectNonKeepAlive(httpResponse);
        replayAll();
        writer.write(context, request, response);
    }

    private void expectContentType( FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set( HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8" );
        expect(this.httpResponse.headers()).andReturn(headers);
//        httpResponse.headers().set( HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8" );
    }

    private void expectSetContent( FullHttpResponse httpResponse, Throwable exception )
    {
//        StringBuilder builder = new StringBuilder( "Failure: " );
//		builder.append( exception.getLocalizedMessage() );
//		builder.append( "\r\n" );
//		httpResponse.setContent(
//                ChannelBuffers.copiedBuffer( builder.toString(),
//                                             Charset.forName( "UTF-8" ) ) );
        expect(this.httpResponse.replace(anyObject(ByteBuf.class))).andReturn(this.httpResponse);
    }

    private void expectNonKeepAlive( FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set( HttpHeaderNames.CONNECTION, "close" );
        expect(this.httpResponse.headers()).andReturn(headers);
    }

    private void expectKeepAlive(ByteBuf content, FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set( HttpHeaderNames.CONTENT_LENGTH, content.readableBytes() );
        expect( httpResponse.content() ).andReturn( content );
        expect(this.httpResponse.headers()).andReturn(headers);
    }

    private static class MockErrorResponseWriter
    extends ErrorResponseWriter
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
