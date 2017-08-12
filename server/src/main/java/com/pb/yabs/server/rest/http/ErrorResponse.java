package com.pb.yabs.server.rest.http;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class ErrorResponse implements Response {
    private final String status = "ERROR";
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
