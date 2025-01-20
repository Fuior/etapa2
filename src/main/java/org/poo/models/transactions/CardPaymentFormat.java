package org.poo.models.transactions;

import lombok.Getter;

@Getter
public class CardPaymentFormat extends Transaction {

    private final double amount;
    private final String commerciant;

    public CardPaymentFormat(final int timestamp, final String description,
                             final double amount, final String commerciant) {

        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
    }
}
