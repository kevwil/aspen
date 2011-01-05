package com.github.kevwil.aspen.domain;

import org.jboss.netty.handler.codec.http.*;

/**
 * @author kevwil
 * @since Jan 04, 2011
 */
public abstract class HttpResponseWriterBase
implements HttpResponseWriter
{
    public HttpResponse createHttpResponse( Response response )
    {
        return new DefaultHttpResponse( HttpVersion.HTTP_1_1, response.getResponseStatus() );
    }
}
