package com.github.kevwil.aspen.domain;

import io.netty.handler.codec.http.HttpResponseStatus;

public class MessageContext {
    private Request request;
    private Response response;

    public MessageContext(Request request, Response response) {
        super();
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }

    public Throwable getException() {
        return getResponse().getException();
    }

    public void setException(Throwable throwable) {
        getResponse().setException(throwable);
    }

    public void setHttpStatus(HttpResponseStatus httpStatus) {
        getResponse().setResponseStatus(httpStatus);
    }
}
