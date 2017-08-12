package com.pb.yabs.commons.dao;


import com.pb.yabs.commons.exception.ErrorCode;
import com.pb.yabs.commons.exception.YabsException;
import com.pb.yabs.commons.model.Account;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class MemoryAccountDAO implements AccountDAO {
    private Map<UUID, Account> accounts = new ConcurrentHashMap<>();

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
    public CompletableFuture<Account> updateAccount(UUID uuid, Function<Account, Account> update){
        return CompletableFuture.completedFuture(accounts.computeIfPresent(uuid, (u, account) -> update.apply(account)));
    }

    @Override
    public CompletableFuture<Account> getAccount(UUID uuid) {
        final CompletableFuture<Account> cf = new CompletableFuture<>();

        if (!accounts.containsKey(uuid)) {
            cf.completeExceptionally(new YabsException(ErrorCode.ACCOUNT_NOT_FOUND, "Account({}) not found", uuid));
        } else {
            cf.complete(accounts.get(uuid));
        }
        return cf;
    }
}
