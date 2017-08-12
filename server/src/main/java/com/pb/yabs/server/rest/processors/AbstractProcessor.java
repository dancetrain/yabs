package com.pb.yabs.server.rest.processors;

import com.sun.net.httpserver.HttpExchange;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.concurrent.CompletableFuture;

abstract class AbstractProcessor implements Processor {

    @Override
    public CompletableFuture<Object> process(HttpExchange httpExchange) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Not Implemented"));
        return future;
    }
}
