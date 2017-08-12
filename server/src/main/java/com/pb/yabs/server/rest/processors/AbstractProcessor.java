package com.pb.yabs.server.rest.processors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import com.sun.net.httpserver.HttpExchange;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
abstract class AbstractProcessor implements Processor {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CompletableFuture<?> process(HttpExchange httpExchange) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Not Implemented"));
        return future;
    }

    <RS> CompletableFuture<RS> processPost(HttpExchange httpExchange, Function<JsonNode, CompletableFuture<RS>> process) {
        final CompletableFuture<RS> rsFuture = new CompletableFuture<>();

        if (!"POST".equals(httpExchange.getRequestMethod())) {
            rsFuture.completeExceptionally(new RuntimeException("Expecting POST request"));
            return rsFuture;
        }

        try {
            final JsonNode jsonNode = Optional.ofNullable(objectMapper.readTree(httpExchange.getRequestBody()))
                    .orElseGet(objectMapper::createObjectNode);
            process.apply(jsonNode).thenAccept(rsFuture::complete);
        } catch (Exception error) {
            rsFuture.completeExceptionally(error);
        }
        return rsFuture;
    }

    UUID getAccountUuidOrThrow(HttpExchange httpExchange) {
        final String[] split = httpExchange.getRequestURI().getPath().split("/");
        if (split.length < 3) {
            throw new RuntimeException("Missing account uuid");
        }
        return UUID.fromString(split[2]);
    }

    JsonNode getFieldOrThrow(JsonNode node, String field) {
        final JsonNode data = node.get(field);
        if (data == null) {
            throw new RuntimeException(String.format("Missing `%s` field. Available fields: %s", field, ImmutableList.copyOf(node.fieldNames())));
        }
        return data;
    }
}
