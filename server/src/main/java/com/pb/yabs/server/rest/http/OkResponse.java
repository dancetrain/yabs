package com.pb.yabs.server.rest.http;

public class OkResponse implements Response {
    private final String status = "OK";
    private final Object result;

    public OkResponse(Object result) {
        this.result = result;
    }
}
