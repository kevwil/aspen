package com.github.kevwil.aspen;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.util.ReferenceCountUtil;

import java.util.List;

public class RequestURLDecoder extends MessageToMessageDecoder<HttpObject> {
    private boolean shouldUpload(HttpObject object) {
        if (object instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) object;
            return (request.method().equals(HttpMethod.POST) && HttpPostRequestDecoder.isMultipart(request));
        } else {
            return false;
        }
    }

    private void removeHandlerIfPresent(ChannelHandlerContext context, String handlerName) {
        if (context.pipeline().get(handlerName) != null) {
            context.pipeline().remove(handlerName);
        }
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, HttpObject object, List<Object> list) throws Exception {
        if (object instanceof HttpRequest) {
            if (shouldUpload(object)) {
                removeHandlerIfPresent(ctx, "aggregator");
                removeHandlerIfPresent(ctx, "DefaultRequestHandler");
            } else {
                removeHandlerIfPresent(ctx, "FileUploadHandler");
            }
        }
        list.add(ReferenceCountUtil.retain(object));
    }
}
