package org.poo.models;

import lombok.Getter;

@Getter
public class Transaction {

    private int timestamp;
    private String description;

    public Transaction(final int timestamp, final String description) {
        this.timestamp = timestamp;
        this.description = description;
    }
}

