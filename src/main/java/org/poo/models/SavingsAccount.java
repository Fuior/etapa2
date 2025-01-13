package org.poo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsAccount extends AccountService {

    private double interestRate;

    public SavingsAccount(final String currency, final String accountType,
                          final int timestamp) {

        super(currency, accountType, timestamp);
    }
}
