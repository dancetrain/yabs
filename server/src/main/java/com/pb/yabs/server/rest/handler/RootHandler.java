package com.pb.yabs.server.rest.handler;

import com.pb.yabs.commons.exception.ErrorCode;
import com.pb.yabs.commons.exception.YabsException;
import com.pb.yabs.server.rest.processors.Processor;
import com.pb.yabs.server.rest.utils.JsonMapper;
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

    private final JsonMapper jsonMapper = new JsonMapper();

    public RootHandler(List<Processor> processors) {
        this.processors = processors.stream()
                .collect(Collectors.toMap(Processor::getEndpoint, Function.identity()));
    }

    public void handle(HttpExchange httpExchange) {
        String endpoint = getEndpoint(httpExchange);
        logger.debug("Processing {} with endpoint {}", httpExchange.getRequestURI(), endpoint);

        Processor processor = processors.get(endpoint);

        if (processor == null) {
            logger.error("Missing processor for endpoint {}", endpoint);
            sendResponse(httpExchange, Response.error(-1, String.format("Missing processor for %s", endpoint)));
        } else {
            processor.process(httpExchange)
                    .thenApply(response -> {
                        logger.debug("Successfully processed {}", endpoint);
                        return Response.ok(response);
                    })
                    .exceptionally(throwable -> {
                        logger.error("Error processing " + endpoint, throwable);
                        return Response.error(getErrorCode(throwable), throwable.getMessage());
                    })
                    .thenAccept(response -> sendResponse(httpExchange, response));
        }
    }

    private void sendResponse(HttpExchange httpExchange, Response response) {
        String data = jsonMapper.toJson(response).orElse("");

        try {
            httpExchange.getResponseHeaders().add("Content-Type", "application/json");
            httpExchange.sendResponseHeaders(200, data.length());
            httpExchange.getResponseBody().write(data.getBytes());
            httpExchange.getResponseBody().close();
        } catch (IOException ignore) {
            // ignore
        }
    }

    private String getEndpoint(HttpExchange httpExchange) {
        String[] parts = httpExchange.getRequestURI().getPath().split("/");

        if (parts.length > 1) {
            return parts[1];
        }

        return "";
    }

    private static int getErrorCode(Throwable throwable) {
        if (throwable == null) {
            return getDefaultErrorCode();
        }
        if (throwable instanceof YabsException) {
            return ((YabsException) throwable).getErrorCode().getValue();
        }
        if (throwable instanceof RuntimeException) {
            return getErrorCode(throwable.getCause());
        }
        return getDefaultErrorCode();
    }

    private static int getDefaultErrorCode() {
        return ErrorCode.UNKNOWN.getValue();
    }

    interface Response {
        static Response ok(Object result) {
            return new OkResponse(result);
        }

        static Response error(int code, String message) {
            return new ErrorResponse(code, message);
        }
    }

    static class OkResponse implements Response {
        private final String status = "OK";
        private final Object result;

        OkResponse(Object result) {
            this.result = result;
        }
    }

    static class ErrorResponse implements Response {
        private final String status = "ERROR";
        private final int code;
        private final String message;

        ErrorResponse(int code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
