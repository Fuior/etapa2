package org.poo.models.transactions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommerciantTransactions {

    private String name;
    private int numberOfTransactions;

    public CommerciantTransactions(final String name) {
        this.name = name;
        this.numberOfTransactions = 0;
    }
}
