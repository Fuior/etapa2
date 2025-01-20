package org.poo.models.transactions;

import lombok.Getter;
import org.poo.fileio.CommandInput;

import java.util.List;

@Getter
public class CustomSplitPayment extends SplitPaymentFormat {

    private final Double[] amountForUsers;
    private final List<String> involvedAccounts;

    public CustomSplitPayment(final CommandInput paymentDetails, final String description,
                             final List<String> involvedAccounts) {

        super(paymentDetails, description);
        this.amountForUsers = paymentDetails.getAmountForUsers().toArray(new Double[0]);
        this.involvedAccounts = involvedAccounts;
    }
}
