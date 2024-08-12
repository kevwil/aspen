package com.github.kevwil.aspen;

import com.github.kevwil.aspen.domain.*;
import com.github.kevwil.aspen.exception.ServiceException;
import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AttributeKey;

/**
 * @author kevwil
 * @since Dec. 23, 2010
 */
public class RackChannelInboundHandler
extends SimpleChannelInboundHandler<FullHttpRequest>
{
    private static final AttributeKey<MessageContext> CONTEXT_KEY = AttributeKey.valueOf("context");

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

//    @Override
//    public boolean acceptInboundMessage(Object msg) throws Exception {
//        return super.acceptInboundMessage(msg);
//    }
//
//    @Override
//    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//        super.channelRead(ctx, msg);
//    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest httpRequest) throws Exception
    {
        MessageContext context = createInitialContext(ctx, httpRequest);
        try
        {
            processRequest(ctx, context);
        }
        catch( Throwable t )
        {
            handleAspenException(ctx, t);
        }
    }

    private MessageContext createInitialContext(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        Request request = createRequest(ctx, httpRequest);
        Response response = createResponse();
        MessageContext context = new MessageContext(request, response);
        ctx.channel().attr(CONTEXT_KEY).set(context);
        return context;
    }

    private Request createRequest(ChannelHandlerContext ctx, FullHttpRequest request) {
        return new Request(ctx, request, rack.getRuntime());
    }

    private Response createResponse() {
        return new Response();
    }

    private void processRequest(ChannelHandlerContext ctx, MessageContext context) {
        Response result = rack.process(context.getRequest());
        if (result.hasException()) {
            writeError(ctx, context.getRequest(), result);
        } else {
            writeResponse(ctx, context.getRequest(), result);
        }
    }

    private void handleAspenException(ChannelHandlerContext ctx, Throwable cause) {
        MessageContext context = ctx.channel().attr(CONTEXT_KEY).get();
        // TODO: map exception types to HttpStatus codes
        if (ServiceException.isAssignableFrom(cause)) {
            context.setHttpStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
        }
        context.setException(cause);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
        super.channelReadComplete(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        try {
            MessageContext context = ctx.channel().attr(CONTEXT_KEY).get();
            if (context != null) {
                context.setException(cause.getCause() != null ? cause.getCause() : cause);
            }
        } catch (Throwable t) {
            t.printStackTrace(System.err);
        } finally {
            ctx.channel().close();
        }
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
    }
}
