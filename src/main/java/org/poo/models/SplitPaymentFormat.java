package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SplitPaymentFormat extends Transaction {

    private String currency;
    private double amount;
    private List<String> involvedAccounts;

    public SplitPaymentFormat(final int timestamp, final String description,
                              final String currency, final double amount,
                              final List<String> involvedAccounts) {

        super(timestamp, description);
        this.currency = currency;
        this.amount = amount;
        this.involvedAccounts = involvedAccounts;
    }
}
