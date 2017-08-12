package com.pb.yabs.server.rest.processors;

import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.CompletableFuture;

public interface Processor {
    String getEndpoint();
    CompletableFuture<Object> process(HttpExchange httpExchange);
}
