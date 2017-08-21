package com.pb.yabs.commons.dao;


import com.pb.yabs.commons.exception.ErrorCode;
import com.pb.yabs.commons.exception.YabsException;
import com.pb.yabs.commons.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.function.Function;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class MemoryAccountDAO implements AccountDAO {
    private Map<UUID, Account> accounts = new ConcurrentHashMap<>();
    private static final Logger logger = LoggerFactory.getLogger(MemoryAccountDAO.class);

    @Override
    public void clear() {
        accounts.clear();
    }

    @Override
    public CompletableFuture<Account> createAccount(double balance) {
        UUID accountUUID = UUID.randomUUID();
        if (accounts.containsKey(accountUUID)) {
            // duplicated key o_O
            return createAccount(balance);
        }
        Account account = new Account(accountUUID, balance);
        accounts.put(account.getUuid(), account);
        return CompletableFuture.completedFuture(account);
    }

    @Override
    public CompletableFuture<Account> updateAccount(UUID uuid, Function<Account, Account> update) {
        final CompletableFuture<Account> cf = new CompletableFuture<>();
        accounts.compute(uuid, (u, account) -> {
            if (account != null) {
                final Account result = update.apply(account);
                cf.complete(result);
                return result;
            } else {
                logger.error("Missing account {}", uuid);
                cf.completeExceptionally(new YabsException(ErrorCode.ACCOUNT_NOT_FOUND, "Missing Account({})", uuid));
                return null;
            }
        });
        return cf;
    }

    @Override
    public CompletableFuture<Account> getAccount(UUID uuid) {
        final CompletableFuture<Account> cf = new CompletableFuture<>();
        final Account account = accounts.get(uuid);
        if (account == null) {
            cf.completeExceptionally(new YabsException(ErrorCode.ACCOUNT_NOT_FOUND, "Account({}) not found", uuid));
        } else {
            cf.complete(account);
        }
        return cf;
    }
}
