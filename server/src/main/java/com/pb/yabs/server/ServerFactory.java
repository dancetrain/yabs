package com.pb.yabs.server;

import com.google.common.collect.ImmutableList;
import com.pb.yabs.commons.dao.MemoryAccountDAO;
import com.pb.yabs.commons.service.AccountService;
import com.pb.yabs.server.rest.handler.RootHandler;
import com.pb.yabs.server.rest.processors.*;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class ServerFactory {
    private final AccountService accountService = new AccountService(new MemoryAccountDAO());

    public HttpServer createServer(int port, int threads) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new RootHandler(listProcessors()));
        server.setExecutor(new ScheduledThreadPoolExecutor(threads));
        return server;
    }

    private List<Processor> listProcessors() {
        return ImmutableList.of(
                new RegistrationProcessor(accountService),
                new DepositProcessor(accountService),
                new WithdrawProcessor(accountService),
                new TransferProcessor(accountService),
                new BalanceProcessor(accountService)
        );
    }

}
