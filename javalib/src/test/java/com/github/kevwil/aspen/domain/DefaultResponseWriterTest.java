package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackChannelUpstreamHandler;
import com.github.kevwil.aspen.RackProxy;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.junit.*;

import java.nio.charset.Charset;

import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jan 04, 2011
 */
public class DefaultResponseWriterTest
{
    private MockDefaultResponseWriter _writer;
    private HttpResponse _httpResponse;
    private HttpRequest _httpRequest;
    private Request _request;
    private Response _response;
    private ChannelHandlerContext _context;
    private RackProxy _rack;

    @Before
    public void setUp()
    {
        _httpResponse = createMock( HttpResponse.class );
        _writer = new MockDefaultResponseWriter();
        _writer.setResponse( _httpResponse );
        _httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "/" );
        _rack = createMock( RackProxy.class );
        ChannelHandler handler = new RackChannelUpstreamHandler( _rack );
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "handler", handler );
        new DefaultLocalServerChannelFactory().newChannel( pipeline );
        _context = pipeline.getContext( handler );
        _request = new Request( _context, _httpRequest );
        _response = new Response( _request );
    }

    @After
    public void tearDown()
    {
        verify( _httpResponse );
        verify( _rack );
    }

    private void replayAll()
    {
        replay( _rack );
        replay( _httpResponse );
    }

    @Test
    public void shouldSetContentIfBodyPresent()
    {
        HttpHeaders.setKeepAlive( _httpRequest, false );
        _httpResponse.setContent( anyObject( ChannelBuffer.class ) );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _response.setBody( "hello" );
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldNotSetContentIfNoBodyPresent()
    {
        HttpHeaders.setKeepAlive( _httpRequest, false );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldSetContentLengthIfKeepAlive()
    {
        _httpResponse.setContent( anyObject( ChannelBuffer.class ) );
        expectKeepAlive( ChannelBuffers.copiedBuffer( "hello\r\n",
                                                      Charset.forName( "UTF-8" ) ),
                         _httpResponse );
        replayAll();
        _response.setBody( "hello" );
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldSetConnectionHeaderIfNotKeepAlive()
    {
        HttpHeaders.setKeepAlive( _httpRequest, false );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    private void expectNonKeepAlive( HttpResponse httpResponse )
    {
        httpResponse.setHeader( HttpHeaders.Names.CONNECTION, "close" );
    }

    private void expectKeepAlive( ChannelBuffer content, HttpResponse httpResponse )
    {
        expect( httpResponse.getContent() ).andReturn( content );
        httpResponse.setHeader( eq( HttpHeaders.Names.CONTENT_LENGTH ), anyObject( String.class ) );
    }

    private class MockDefaultResponseWriter
    extends DefaultResponseWriter
    {
        private HttpResponse _response;

        public void setResponse( final HttpResponse response )
        {
            _response = response;
        }

        @Override
        public HttpResponse createHttpResponse( final Response response )
        {
            return _response;
        }
    }
}
