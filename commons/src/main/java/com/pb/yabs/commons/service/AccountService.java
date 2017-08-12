package com.pb.yabs.commons.service;

import com.pb.yabs.commons.dao.AccountDAO;
import com.pb.yabs.commons.exception.ErrorCode;
import com.pb.yabs.commons.exception.YabsException;
import com.pb.yabs.commons.model.Account;
import com.pb.yabs.commons.model.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class AccountService {
    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    private final AccountDAO accountDAO;

    public AccountService(AccountDAO accountDAO) {
        this.accountDAO = accountDAO;
    }

    public CompletableFuture<Account> createAccountAsync(double balance) {
        logger.debug("Creating account with {}", balance);
        return accountDAO.createAccount(balance);
    }

    public CompletableFuture<Account> depositAsync(UUID accountUUID, double amount) {
        return accountDAO.updateAccount(accountUUID, account -> {
            return account.apply(amount);
        });
    }

    public CompletableFuture<Account> withdrawAsync(UUID accountUUID, double amount) {
        return accountDAO.updateAccount(accountUUID, account -> {
            if (account.getBalance() < amount) {
                throw new YabsException(ErrorCode.NOT_ENOUGH_MONEY, "{} is not enough to transfer {}", account.getBalance(), amount);
            }
            return account.apply(-amount);
        });
    }

    public CompletableFuture<Transfer> validateAndAcceptTransferAsync(Transfer transfer) {
        return withdrawAsync(transfer.getSender(), transfer.getAmount())
                .thenCompose(sender -> depositAsync(transfer.getRecipient(), transfer.getAmount()))
                .thenApply(recipient -> transfer);
    }

    public CompletableFuture<Account> fetchAccountAsync(UUID uuid) {
        return accountDAO.getAccount(uuid);
    }

    public <T> Optional<T> waitAsync(CompletableFuture<T> future) {
        try {
            return Optional.of(future.get(5, TimeUnit.SECONDS));
        } catch (ExecutionException e) {
            return Optional.empty();
        } catch (InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    public void clear() {
        accountDAO.clear();
    }
}
