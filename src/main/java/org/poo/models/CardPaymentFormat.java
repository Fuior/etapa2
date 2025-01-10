package org.poo.models;

import lombok.Getter;

@Getter
public class CardPaymentFormat extends Transaction {

    private double amount;
    private String commerciant;

    public CardPaymentFormat(final int timestamp, final String description,
                             final double amount, final String commerciant) {

        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
    }
}
