package com.github.kevwil.aspen.domain;

import io.netty.handler.codec.http.*;

/**
 * @author kevwil
 * @since Jan 04, 2011
 */
public abstract class HttpResponseWriterBase
implements HttpResponseWriter
{
    public FullHttpResponse createHttpResponse( Response response )
    {
        return new DefaultFullHttpResponse( HttpVersion.HTTP_1_1, response.getResponseStatus() );
    }
}
