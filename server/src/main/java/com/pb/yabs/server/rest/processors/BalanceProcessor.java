package com.pb.yabs.server.rest.processors;

import com.pb.yabs.commons.service.AccountService;
import com.sun.net.httpserver.HttpExchange;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class BalanceProcessor extends AbstractProcessor {
    private final AccountService accountService;
    public static String BALANCE_ENDPOINT = "balance";

    public BalanceProcessor(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getEndpoint() {
        return BALANCE_ENDPOINT;
    }

    @Override
    public CompletableFuture<?> process(HttpExchange httpExchange) {
        return accountService.fetchAccountAsync(getAccountUuidOrThrow(httpExchange));
    }
}
