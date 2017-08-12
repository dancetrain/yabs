package com.pb.yabs.server.rest.http;

public class ErrorResponse implements Response {
    private final String status = "ERROR";
    private final String message;

    public ErrorResponse(String message) {
        this.message = message;
    }
}
