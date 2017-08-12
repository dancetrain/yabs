package com.pb.yabs.server.rest.processors;

import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.CompletableFuture;

public class RegistrationProcessor extends AbstractProcessor {
    @Override
    public String getEndpoint() {
        return "registration";
    }

    @Override
    public CompletableFuture<Object> process(HttpExchange httpExchange) {
        return CompletableFuture.completedFuture("registered");
    }
}
