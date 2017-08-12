package com.pb.yabs.commons.model;

import java.util.UUID;

/**
 * @author Pavel Borsky
 *         date 2017-08-12
 */
public class Transfer {

    private final UUID sender;
    private final UUID recipient;
    private final double amount;

    public Transfer(UUID sender, UUID recipient, double amount) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
    }

    public UUID getSender() {
        return sender;
    }

    public UUID getRecipient() {
        return recipient;
    }

    public double getAmount() {
        return amount;
    }
}
