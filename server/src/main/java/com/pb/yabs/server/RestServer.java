package com.pb.yabs.server;

import com.sun.net.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class RestServer {
    private final static Logger logger = LoggerFactory.getLogger(RestServer.class);

    private final HttpServer server;

    public RestServer(HttpServer server) {
        this.server = server;
    }

    public void start() {
        logger.warn("RestServer started at {}", server.getAddress());
        server.start();
    }

    public void stop() {
        logger.warn("RestServer stopping...");
        server.stop(5);
        logger.warn("RestServer stopped");
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            // TODO: print usage
            System.exit(1);
        } else {
            ServerFactory restServerFactory = new ServerFactory();
            RestServer server = new RestServer(restServerFactory.createServer(Integer.parseInt(args[0]), 16));
            server.start();
        }
    }
}
