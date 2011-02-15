package com.github.kevwil.aspen.domain;

import com.github.kevwil.aspen.RackChannelUpstreamHandler;
import com.github.kevwil.aspen.RackProxy;
import com.github.kevwil.aspen.exception.ServiceException;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.local.DefaultLocalServerChannelFactory;
import org.jboss.netty.handler.codec.http.*;
import org.jruby.Ruby;
import org.junit.*;

import java.nio.charset.Charset;

import static org.easymock.EasyMock.*;

/**
 * @author kevwil
 * @since Jan 05, 2011
 */
public class ErrorResponseWriterTest
{
    private MockErrorResponseWriter _writer;
    private HttpResponse _httpResponse;
    private HttpRequest _httpRequest;
    private Request _request;
    private Response _response;
    private ChannelHandlerContext _context;
    private RackProxy _rack;
    private Throwable _exception;

    @Before
    public void setUp()
    {
        _httpResponse = createMock( HttpResponse.class );
        _writer = new MockErrorResponseWriter();
        _writer.setResponse( _httpResponse );
        _httpRequest = new DefaultHttpRequest( HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost/" );
        _exception = new ServiceException( "Oops!" );
        _rack = createMock( RackProxy.class );
        ChannelHandler handler = new RackChannelUpstreamHandler( _rack );
        ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast( "handler", handler );
        new DefaultLocalServerChannelFactory().newChannel( pipeline );
        _context = pipeline.getContext( handler );
        _request = new Request( _context, _httpRequest, Ruby.getGlobalRuntime() );
        _response = new Response( _request );
        _response.setException( _exception );
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
    public void shouldSetContentLengthIfKeepAlive()
    {
        expectContentType( _httpResponse );
        expectSetContent( _httpResponse, _exception );
        expectKeepAlive( ChannelBuffers.copiedBuffer( "hello\r\n", Charset.forName( "UTF-8" ) ),
                         _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    @Test
    public void shouldSetConnectionHeaderIfNotKeepAlive()
    {
        HttpHeaders.setKeepAlive( _httpRequest, false );
        expectContentType( _httpResponse );
        expectSetContent( _httpResponse, _exception );
        expectNonKeepAlive( _httpResponse );
        replayAll();
        _writer.write( _context, _request, _response );
    }

    private void expectContentType( HttpResponse httpResponse )
    {
        httpResponse.setHeader( HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8" );
    }

    private void expectSetContent( HttpResponse httpResponse, Throwable exception )
    {
//        StringBuilder builder = new StringBuilder( "Failure: " );
//		builder.append( exception.getLocalizedMessage() );
//		builder.append( "\r\n" );
//		httpResponse.setContent(
//                ChannelBuffers.copiedBuffer( builder.toString(),
//                                             Charset.forName( "UTF-8" ) ) );
        httpResponse.setContent( anyObject( ChannelBuffer.class ) );
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

    private class MockErrorResponseWriter
    extends ErrorResponseWriter
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
