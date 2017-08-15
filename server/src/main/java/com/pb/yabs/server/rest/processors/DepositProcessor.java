package com.pb.yabs.server.rest.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.pb.yabs.commons.service.AccountService;
import com.sun.net.httpserver.HttpExchange;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class DepositProcessor extends AbstractProcessor {
    private final AccountService accountService;
    public static String DEPOSIT_ENDPOINT = "deposit";

    public DepositProcessor(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getEndpoint() {
        return DEPOSIT_ENDPOINT;
    }

    @Override
    public CompletableFuture<?> process(HttpExchange httpExchange) {
        return processPost(httpExchange, jsonNode -> {
            JsonNode amount = getFieldOrThrow(jsonNode, "amount");
            UUID profileUUID = getAccountUuidOrThrow(httpExchange);
            return accountService.depositAsync(profileUUID, amount.asDouble());
        });
    }
}
