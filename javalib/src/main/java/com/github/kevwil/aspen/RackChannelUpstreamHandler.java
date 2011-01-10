package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.*;
import com.github.kevwil.aspen.exception.ServiceException;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.HttpRequest;

/**
 * @author kevwil
 * @since Dec. 23, 2010
 */
public class RackChannelUpstreamHandler
extends SimpleChannelUpstreamHandler
{
    private RackProxy _rack;
    private HttpResponseWriter _responseWriter;
    private HttpResponseWriter _errorWriter;

    public RackChannelUpstreamHandler( final RackProxy rackProxy )
    {
        _rack = rackProxy;
        _responseWriter = new DefaultResponseWriter();
        _errorWriter = new ErrorResponseWriter();
    }

    @Override
    public void messageReceived( final ChannelHandlerContext ctx, final MessageEvent e )
    throws Exception
    {
        Request request = createRequest( ctx, (HttpRequest)e.getMessage() );
        Response response = createResponse( request );
        try
        {
            response = _rack.process( request );
            if( response == null )
            {
                response = createResponse( request );
                response.setException( new ServiceException( "null response from Rack" ) );
            }
            if( !response.hasException() && response.getResponseStatus().getCode() >= 400 )
            {
                response.setException( new ServiceException( response.getResponseStatus() ) );
            }
        }
        catch( Exception ex )
        {
            assert response != null;
            response.setException( ex );
        }
        finally
        {
            assert response != null;
            if( response.hasException() )
            {
                writeError( ctx, request, response );
            }
            else
            {
                writeResponse( ctx, request, response );
            }
        }
    }

    private Request createRequest( final ChannelHandlerContext ctx, final HttpRequest message )
    {
        return new Request( ctx, message );
    }

    private Response createResponse( final Request request )
    {
        return new Response( request );
    }

    @Override
    public void exceptionCaught( final ChannelHandlerContext ctx, final ExceptionEvent e )
    throws Exception
    {
        Throwable cause = e.getCause();

        Request request = new Request( ctx, null );
        Response response = new Response( request );
        response.setException( cause );
        
        writeError( ctx, request, response );
    }

    private void writeResponse( final ChannelHandlerContext ctx, final Request request, final Response response )
    {
        _responseWriter.write( ctx, request, response );
    }

    private void writeError( final ChannelHandlerContext ctx, final Request request, final Response response )
    {
        _errorWriter.write( ctx, request, response );
    }
}
