package com.pb.yabs.server.rest.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.pb.yabs.commons.model.Transfer;
import com.pb.yabs.commons.service.AccountService;
import com.sun.net.httpserver.HttpExchange;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class TransferProcessor extends AbstractProcessor {
    private final AccountService accountService;
    public static String TRANSFER_ENDPOINT = "transfer";

    public TransferProcessor(AccountService accountService) {
        this.accountService = accountService;
    }

    @Override
    public String getEndpoint() {
        return TRANSFER_ENDPOINT;
    }

    @Override
    public CompletableFuture<?> process(HttpExchange httpExchange) {
        return processPost(httpExchange, jsonNode -> {
            JsonNode amount = getFieldOrThrow(jsonNode, "amount");
            JsonNode recipient = getFieldOrThrow(jsonNode, "recipient");
            UUID profileUUID = getAccountUuidOrThrow(httpExchange);
            Transfer transfer = new Transfer(profileUUID, UUID.fromString(recipient.textValue()), amount.asDouble());

            return accountService.validateAndAcceptTransferAsync(transfer);
        });
    }
}
