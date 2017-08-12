package com.pb.yabs.server.rest.processors;

import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public interface Processor {
    String getEndpoint();
    CompletableFuture<?> process(HttpExchange httpExchange);
}
