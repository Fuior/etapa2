package org.poo.models;

import lombok.Getter;
import org.poo.fileio.CommandInput;

import java.util.List;

@Getter
public class EqualSplitPayment extends SplitPaymentFormat {

    private final double amount;
    private final List<String> involvedAccounts;

    public EqualSplitPayment(final CommandInput paymentDetails, final String description,
                             final double amount, final List<String> involvedAccounts) {

        super(paymentDetails, description);
        this.amount = amount;
        this.involvedAccounts = involvedAccounts;
    }
}
