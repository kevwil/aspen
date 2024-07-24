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
    private MockErrorResponseWriter _writer;
    private FullHttpResponse _httpResponse;
    private FullHttpRequest _httpRequest;
    private Request _request;
    private Response _response;
    private ChannelHandlerContext _context;
    private Throwable _exception;
    private Channel _channel;
    private ChannelPipeline _pipeline;
    private ChannelFuture _future;

    @Before
    public void setUp()
    {
        _writer = new MockErrorResponseWriter();
        _channel = createMock(Channel.class);
        _pipeline = createMock(ChannelPipeline.class);
        _context = createMock(ChannelHandlerContext.class);
        _future = createMock(ChannelFuture.class);
        _httpResponse = createMock( FullHttpResponse.class );
        _writer.setResponse( _httpResponse );
        _httpRequest = new DefaultFullHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        _exception = new ServiceException( "Oops!" );
        _request = new Request( _context, _httpRequest, Ruby.getGlobalRuntime() );
        _response = new Response( _request );
        _response.setException( _exception );

        expect(_context.channel()).andReturn(_channel);
        expect(_channel.write(anyObject(FullHttpResponse.class))).andReturn(_future);
        expect(_future.addListener(anyObject())).andReturn(_future);
        expect(_httpResponse.touch(anyObject())).andReturn(_httpResponse).anyTimes();
    }

    @After
    public void tearDown()
    {
        verify( _httpResponse );
        verify( _channel );
        verify( _pipeline );
        verify( _context );
        verify( _future );
    }

    private void replayAll()
    {
        replay( _httpResponse );
        replay( _channel );
        replay( _pipeline );
        replay( _context );
        replay( _future );
    }

    @Test
    public void shouldSetContentLengthIfKeepAlive()
    {
        expectContentType( _httpResponse );
        expectSetContent( _httpResponse, _exception );
        expectKeepAlive( Unpooled.copiedBuffer( "hello\r\n", StandardCharsets.UTF_8 ),
                         _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldSetConnectionHeaderIfNotKeepAlive()
    {
        HttpUtil.setKeepAlive( _httpRequest, false );
        expectContentType( _httpResponse );
        expectSetContent( _httpResponse, _exception );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    private void expectContentType( FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set( HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8" );
        expect(_httpResponse.headers()).andReturn(headers);
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
        expect(_httpResponse.replace(anyObject(ByteBuf.class))).andReturn(_httpResponse);
    }

    private void expectNonKeepAlive( FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set( HttpHeaderNames.CONNECTION, "close" );
        expect(_httpResponse.headers()).andReturn(headers);
    }

    private void expectKeepAlive(ByteBuf content, FullHttpResponse httpResponse )
    {
        HttpHeaders headers = new DefaultHttpHeaders();
        headers.set( HttpHeaderNames.CONTENT_LENGTH, content.readableBytes() );
        expect( httpResponse.content() ).andReturn( content );
        expect(_httpResponse.headers()).andReturn(headers);
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
