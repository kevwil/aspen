package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.*;
import com.github.kevwil.aspen.exception.ServiceException;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;

/**
 * @author kevwil
 * @since Dec. 23, 2010
 */
public class RackChannelInboundHandler
extends SimpleChannelInboundHandler<FullHttpRequest>
{
    private final RackProxy _rack;
    private final HttpResponseWriter _responseWriter;
    private final HttpResponseWriter _errorWriter;

    public RackChannelInboundHandler(final RackProxy rackProxy )
    {
        _rack = rackProxy;
        _responseWriter = new DefaultResponseWriter();
        _errorWriter = new ErrorResponseWriter();
    }

    private void writeResponse( final ChannelHandlerContext ctx, final Request request, final Response response )
    {
        _responseWriter.write( ctx, request, response );
    }

    private void writeError( final ChannelHandlerContext ctx, final Request request, final Response response )
    {
        _errorWriter.write( ctx, request, response );
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception
    {
        Request request = new Request( ctx, httpRequest, _rack.getRuntime() );
        Response response = new Response( request );
        try
        {
            response = _rack.process( request );
            if( response == null )
            {
                response = new Response( request );
                response.setException( new ServiceException( "null response from Rack" ) );
            }
            if( !response.hasException() && response.getResponseStatus().code() >= 400 )
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
}
