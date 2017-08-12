package com.pb.yabs.server;

import com.google.common.collect.ImmutableList;
import com.pb.yabs.server.rest.handler.RootHandler;
import com.pb.yabs.server.rest.processors.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class RestServer {

    private final HttpServer server;

    RestServer(int port) throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
    }

    void setupContext(List<Processor> processors) {
        server.createContext("/", new RootHandler(processors));
    }

    public void start() {
        server.start();
    }

    public void stop() {
        server.stop(5);
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            // TODO: print usage
            System.exit(1);
        } else {
            RestServer server = new RestServer(Integer.parseInt(args[0]));
            server.setupContext(listProcessors());
            server.start();
        }
    }

    private static List<Processor> listProcessors() {
        return ImmutableList.of(
                new RegistrationProcessor(),
                new DepositProcessor(),
                new WithdrawProcessor(),
                new TransferProcessor(),
                new BalanceProcessor()
        );
    }
}
