package org.poo.models;

import lombok.Getter;

@Getter
public class CashWithdrawalFormat extends Transaction {

    private final double amount;

    public CashWithdrawalFormat(final int timestamp, final String description,
                                final double amount) {

        super(timestamp, description);
        this.amount = amount;
    }
}
