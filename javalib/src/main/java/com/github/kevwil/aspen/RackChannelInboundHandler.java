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
    private final RackProxy rack;
    private final HttpResponseWriter responseWriter;
    private final HttpResponseWriter errorWriter;

    public RackChannelInboundHandler(final RackProxy rackProxy )
    {
        super();
        rack = rackProxy;
        responseWriter = new DefaultResponseWriter();
        errorWriter = new ErrorResponseWriter();
    }

    private void writeResponse( final ChannelHandlerContext ctx, final Request request, final Response response )
    {
        responseWriter.write( ctx, request, response );
    }

    private void writeError( final ChannelHandlerContext ctx, final Request request, final Response response )
    {
        errorWriter.write( ctx, request, response );
    }

    @Override
    public boolean acceptInboundMessage(Object msg) throws Exception {
        return super.acceptInboundMessage(msg);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception
    {
        Request request = new Request( ctx, httpRequest, rack.getRuntime() );
        Response response = new Response( request );
        try
        {
            response = rack.process( request );
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }
}
