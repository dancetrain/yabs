package com.pb.yabs.server.rest.processors;

import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.CompletableFuture;

public class DepositProcessor extends AbstractProcessor {
    @Override
    public String getEndpoint() {
        return "deposit";
    }
}
