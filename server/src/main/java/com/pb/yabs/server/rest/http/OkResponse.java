package com.pb.yabs.server.rest.http;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class OkResponse implements Response {
    private final String status = "OK";
    private final Object result;

    public OkResponse(Object result) {
        this.result = result;
    }
}
