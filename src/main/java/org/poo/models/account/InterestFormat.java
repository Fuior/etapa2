package org.poo.models.account;

import lombok.Getter;
import org.poo.models.transactions.Transaction;

@Getter
public class InterestFormat extends Transaction {

    private final double amount;
    private final String currency;

    public InterestFormat(final int timestamp, final String description,
                          final double amount, final String currency) {

        super(timestamp, description);
        this.amount = amount;
        this.currency = currency;
    }
}
