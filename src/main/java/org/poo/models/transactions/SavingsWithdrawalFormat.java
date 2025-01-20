package org.poo.models.transactions;

import lombok.Getter;
import org.poo.fileio.CommandInput;

@Getter
public class SavingsWithdrawalFormat extends Transaction {

    private final String savingsAccountIBAN;
    private final String classicAccountIBAN;
    private final double amount;

    public SavingsWithdrawalFormat(final CommandInput withdrawalDetails,
                                   final String classicAccountIBAN) {

        super(withdrawalDetails.getTimestamp(), "Savings withdrawal");
        this.savingsAccountIBAN = withdrawalDetails.getAccount();
        this.classicAccountIBAN = classicAccountIBAN;
        this.amount = withdrawalDetails.getAmount();
    }
}
