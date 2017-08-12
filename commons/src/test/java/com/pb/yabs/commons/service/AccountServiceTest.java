package com.pb.yabs.commons.service;

import com.pb.yabs.commons.dao.MemoryAccountDAO;
import com.pb.yabs.commons.exception.YabsException;
import com.pb.yabs.commons.model.Account;
import com.pb.yabs.commons.model.Transfer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class AccountServiceTest {
    private AccountService accountService;

    @Before
    public void setUp() throws Exception {
        accountService = new AccountService(new MemoryAccountDAO());
    }

    @After
    public void tearDown() throws Exception {
        accountService.clear();
    }

    @Test
    public void createAccountAsync() throws Exception {
        final Optional<Account> account = accountService.waitAsync(accountService.createAccountAsync(90.0));

        assertEquals("Account not created", true, account.isPresent());
        assertEquals("Account wrong balance", 90.0, account.get().getBalance(), 0.0);
    }

    @Test
    public void testTransferAll() throws Exception {
        Account sender = createAccount(100.0);
        Account recipient = createAccount(100.0);

        Transfer transfer = new Transfer(sender.getUuid(), recipient.getUuid(), sender.getBalance());
        accountService.waitAsync(accountService.validateAndAcceptTransferAsync(transfer));

        sender = accountService.waitAsync(accountService.fetchAccountAsync(sender.getUuid())).get();
        recipient = accountService.waitAsync(accountService.fetchAccountAsync(recipient.getUuid())).get();

        assertEquals("Wrong sender balance", 0.0, sender.getBalance(), 0.0);
        assertEquals("Wrong recipient balance", 200.0, recipient.getBalance(), 0.0);
    }

    @Test(expected = YabsException.class)
    public void testNegativeDeposit() throws Exception {
        Account account = createAccount(0.0);
        accountService.waitAsync(accountService.depositAsync(account.getUuid(), -10.0));
    }

    @Test(expected = YabsException.class)
    public void testNegativeWithdraw() throws Exception {
        Account account = createAccount(50.0);
        accountService.waitAsync(accountService.withdrawAsync(account.getUuid(), -10.0));
    }

    private Account createAccount(double balance) {
        return accountService.waitAsync(accountService.createAccountAsync(balance)).get();
    }

    private <T> CompletableFuture<T> execute(ScheduledExecutorService executor, Supplier<T> supplier) {
        final CompletableFuture<T> cf = new CompletableFuture<>();
        executor.schedule(() -> {
            final T v = supplier.get();
            cf.complete(v);
        }, ThreadLocalRandom.current().nextLong(20) + 1, TimeUnit.MILLISECONDS);
        return cf;
    }

}
