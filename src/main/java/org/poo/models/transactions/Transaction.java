package org.poo.models.transactions;

import lombok.Getter;

@Getter
public class Transaction {

    private final int timestamp;
    private final String description;

    public Transaction(final int timestamp, final String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}

