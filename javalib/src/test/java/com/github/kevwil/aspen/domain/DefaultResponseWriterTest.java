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
    private MockDefaultResponseWriter _writer;
    private FullHttpResponse _httpResponse;
    private FullHttpRequest _httpRequest;
    private Request _request;
    private Response _response;
    private ChannelHandlerContext _context;
    private Channel _channel;
    private ChannelPipeline _pipeline;
    private ChannelFuture _future;

    @Before
    public void setUp()
    {
        _writer = new MockDefaultResponseWriter();
        _httpResponse = createMock( FullHttpResponse.class );
        _channel = createMock(Channel.class);
        _pipeline = createMock(ChannelPipeline.class);
        _context = createMock(ChannelHandlerContext.class);
        _future = createMock(ChannelFuture.class);

        expect(_context.channel()).andReturn(_channel);
        expect(_channel.write(anyObject(FullHttpResponse.class))).andReturn(_future);
        expect(_future.addListener(anyObject())).andReturn(_future);
        expect(_httpResponse.touch(anyObject())).andReturn(_httpResponse).anyTimes();

        _writer.setResponse( _httpResponse );
        _httpRequest = new DefaultFullHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );

        _request = new Request( _context, _httpRequest, Ruby.getGlobalRuntime() );
        _response = new Response( _request );
    }

    @After
    public void tearDown() {
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
    public void shouldSetContentIfBodyPresent()
    {
        HttpUtil.setKeepAlive( _httpRequest, false );
        expect(_httpResponse.replace(anyObject(ByteBuf.class))).andReturn(_httpResponse);
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _response.setBody( "hello" );
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldNotSetContentIfNoBodyPresent()
    {
        HttpUtil.setKeepAlive( _httpRequest, false );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldSetContentLengthIfKeepAlive()
    {
        expect(_httpResponse.replace(anyObject(ByteBuf.class))).andReturn(_httpResponse);
        expectKeepAlive( Unpooled.copiedBuffer( "hello\r\n",
                                                      StandardCharsets.UTF_8 ),
                         _httpResponse );
        replayAll();
        _response.setBody( "hello" );
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldSetConnectionHeaderIfNotKeepAlive()
    {
        HttpUtil.setKeepAlive( _httpRequest, false );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
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
