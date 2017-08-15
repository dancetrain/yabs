package com.pb.yabs.server.rest.processors;

import com.pb.yabs.commons.model.Account;
import com.pb.yabs.commons.service.AccountService;
import com.sun.net.httpserver.HttpExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class RegistrationProcessor extends AbstractProcessor {
    private static final Logger logger = LoggerFactory.getLogger(RegistrationProcessor.class);
    public static String REGISTRATION_ENDPOINT = "registration";

    private final AccountService accountService;

    public RegistrationProcessor(AccountService accountService) {

        this.accountService = accountService;
    }

    @Override
    public String getEndpoint() {
        return REGISTRATION_ENDPOINT;
    }

    @Override
    public CompletableFuture<Account> process(HttpExchange httpExchange) {
        return processPost(httpExchange, jsonNode -> {
            logger.debug("Processing {}", jsonNode);
            return accountService.createAccountAsync(jsonNode.path("balance").asDouble());
        });
    }
}
