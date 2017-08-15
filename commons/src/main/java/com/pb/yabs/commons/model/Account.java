package com.pb.yabs.commons.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.concurrent.Immutable;
import java.util.UUID;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
@Immutable
public class Account {

    private final UUID uuid;
    private final double balance;

    @JsonCreator
    public Account(@JsonProperty("uuid") UUID uuid, @JsonProperty("balance") double balance) {
        this.uuid = uuid;
        this.balance = balance;
    }

    public UUID getUuid() {
        return uuid;
    }

    public double getBalance() {
        return balance;
    }

    public Account apply(double amount) {
        return new Account(uuid, this.balance + amount);
    }

    @Override
    public String toString() {
        return "Account{" +
                "uuid=" + uuid +
                ", balance=" + balance +
                '}';
    }
}
