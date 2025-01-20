package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

@Getter
@Setter
public class SavingsAccount extends AccountService {

    private double interestRate;

    public SavingsAccount(final CommandInput accountDetails) {
        super(accountDetails);
    }
}
