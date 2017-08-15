package com.pb.yabs.server;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pb.yabs.commons.model.Account;
import com.pb.yabs.server.rest.processors.*;
import com.pb.yabs.server.rest.utils.JsonMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class RestServerTest {
    private CloseableHttpClient client;
    private final JsonMapper jsonMapper = new JsonMapper();

    private RestServer restServer;
    private int port = 9000;

    @Before
    public void setUp() throws Exception {
        final ServerFactory serverFactory = new ServerFactory();
        restServer = new RestServer(serverFactory.createServer(port));
        restServer.start();
        client = HttpClients.createDefault();
    }

    @After
    public void tearDown() throws Exception {
        client.close();
        restServer.stop();
    }

    @Test
    public void testRegistration() throws Exception {
        HttpPost rq = preparePostRequest(buildEndpointUrl(RegistrationProcessor.REGISTRATION_ENDPOINT), "balance", 99.0);
        Account account = processRequestOrFail(rq);
        assertEquals("Wrong initial balance", 99.0, account.getBalance(), 0.0);
    }

    @Test
    public void testFlow() throws Exception {
        Account user1 = processRequestOrFail(preparePostRequest(buildEndpointUrl(RegistrationProcessor.REGISTRATION_ENDPOINT)));
        Account user2 = processRequestOrFail(preparePostRequest(buildEndpointUrl(RegistrationProcessor.REGISTRATION_ENDPOINT)));

        user1 = processRequestOrFail(preparePostRequest(buildEndpointUrl(DepositProcessor.DEPOSIT_ENDPOINT, user1.getUuid()), "amount", 58.0));
        assertEquals("Failed deposit",58.0, user1.getBalance(), 0.0);

        user1 = processRequestOrFail(preparePostRequest(buildEndpointUrl(TransferProcessor.TRANSFER_ENDPOINT, user1.getUuid()), "amount", 20, "recipient", user2.getUuid()));
        assertEquals("Failed transfer",38.0, user1.getBalance(), 0.0);

        user2 = processRequestOrFail(new HttpGet(buildEndpointUrl(BalanceProcessor.BALANCE_ENDPOINT, user2.getUuid())));
        assertEquals("Failed balance",20.0, user2.getBalance(), 0.0);

        user2 = processRequestOrFail(preparePostRequest(buildEndpointUrl(WithdrawProcessor.WITHDRAW_ENDPOINT, user2.getUuid()), "amount", user2.getBalance()));
        assertEquals("Failed balance",0.0, user2.getBalance(), 0.0);
    }

    private HttpPost preparePostRequest(String url, Object... params) {
        HttpPost rq = new HttpPost(url);
        if (params != null) {
            ObjectNode body = jsonMapper.getObjectMapper().createObjectNode();
            for(int i = 0; i < params.length; i += 2) {
                body.set(params[i].toString(), jsonMapper.getObjectMapper().valueToTree(params[i + 1]));
            }
            rq.setEntity(new StringEntity(body.toString(), StandardCharsets.UTF_8));
        }
        return rq;
    }

    private Account processRequestOrFail(HttpUriRequest rq) throws IOException {
        try (CloseableHttpResponse response = client.execute(rq)) {
            JsonNode tree = jsonMapper.getObjectMapper().readTree(response.getEntity().getContent());

            assertNotEquals("Wrong response", null, tree);
            assertEquals("Wrong response status", "OK", tree.path("status").asText());

            Account account = jsonMapper.getObjectMapper().treeToValue(tree.path("result"), Account.class);
            assertNotEquals("Wrong response result", null, account);
            return account;
        }
    }

    private String buildEndpointUrl(String endpoint) {
        return String.format("http://localhost:%d/%s", port, endpoint);
    }

    private String buildEndpointUrl(String endpoint, UUID uuid) {
        return String.format("http://localhost:%d/%s/%s", port, endpoint, uuid.toString());
    }
}