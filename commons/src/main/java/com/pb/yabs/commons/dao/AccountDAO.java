package com.pb.yabs.commons.dao;


import com.pb.yabs.commons.model.Account;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public interface AccountDAO {

    void clear();
    CompletableFuture<Account> createAccount(double balance);
    CompletableFuture<Account> getAccount(UUID uuid);
    CompletableFuture<Account> updateAccount(UUID uuid, Function<Account, Account> update);
}
