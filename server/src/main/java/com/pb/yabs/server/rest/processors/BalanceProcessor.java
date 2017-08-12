package com.pb.yabs.server.rest.processors;

import com.pb.yabs.commons.service.AccountService;
import com.sun.net.httpserver.HttpExchange;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class BalanceProcessor extends AbstractProcessor {
    private final AccountService accountService;

    public BalanceProcessor(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getEndpoint() {
        return "balance";
    }

    @Override
    public CompletableFuture<?> process(HttpExchange httpExchange) {
        return accountService.fetchAccountAsync(getAccountUuidOrThrow(httpExchange));
    }
}
