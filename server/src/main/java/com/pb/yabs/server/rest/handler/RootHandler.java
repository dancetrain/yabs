package com.pb.yabs.server.rest.handler;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pb.yabs.server.rest.http.ErrorResponse;
import com.pb.yabs.server.rest.http.OkResponse;
import com.pb.yabs.server.rest.http.Response;
import com.pb.yabs.server.rest.processors.Processor;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class RootHandler implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(RootHandler.class);
    private final Map<String, Processor> processors;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public RootHandler(List<Processor> processors) {
        this.processors = processors.stream()
                .collect(Collectors.toMap(Processor::getEndpoint, Function.identity()));

        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public void handle(HttpExchange httpExchange) throws IOException {
        String endpoint = getEndpoint(httpExchange);
        logger.debug("Processing {} with endpoint {}", httpExchange.getRequestURI(), endpoint);

        Processor processor = processors.get(endpoint);

        if (processor == null) {
            logger.error("Missing endpoint");
            // TODO: Notify about missing endpoint
            httpExchange.getResponseBody().close();
        } else {
            processor.process(httpExchange)
                    .thenApply(response -> {
                        logger.debug("Successfully processed {}", endpoint);
                        Response rs = new OkResponse(response);

                        return rs;
                    })
                    .exceptionally(throwable -> {
                        logger.error("Error processing " + endpoint, throwable);

                        return new ErrorResponse(throwable.getMessage());
                    })
                    .thenAccept(response -> {
                        String data;
                        try {
                            data = objectMapper.writeValueAsString(response);
                        } catch (JsonProcessingException e) {
                            logger.error("Error processing json", e);
                            data = "";
                        }

                        try {
                            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
                            httpExchange.sendResponseHeaders(200, data.length());
                            httpExchange.getResponseBody().write(data.getBytes());
                            httpExchange.getResponseBody().close();
                        } catch (IOException ignore) {
                            // ignore
                        }
                    });
        }
    }

    private String getEndpoint(HttpExchange httpExchange) {
        String[] parts = httpExchange.getRequestURI().getPath().split("/");

        if (parts.length > 1) {
            return parts[1];
        }

        return "";
    }
}
