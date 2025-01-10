package org.poo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingAccount extends AccountService {

    private double interestRate;

    public SavingAccount(final String currency, final String accountType,
                         final int timestamp) {

        super(currency, accountType, timestamp);
    }
}
