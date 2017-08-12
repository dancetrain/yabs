package com.pb.yabs.commons.model;

import java.util.UUID;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class Account {

    private final UUID uuid;
    private final double balance;

    public Account(UUID uuid, double amount) {
        this.uuid = uuid;
        this.balance = amount;
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
