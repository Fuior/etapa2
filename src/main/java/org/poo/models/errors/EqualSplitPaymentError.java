package org.poo.models.errors;

import lombok.Getter;
import org.poo.models.transactions.EqualSplitPayment;

import java.util.List;

@Getter
public class EqualSplitPaymentError extends SplitPaymentError {

    private final double amount;
    private final List<String> involvedAccounts;
    private final String error;

    public EqualSplitPaymentError(final String description, final String error,
                                  final EqualSplitPayment paymentInput) {

        super(description, paymentInput);
        this.amount = paymentInput.getAmount();
        this.involvedAccounts = paymentInput.getInvolvedAccounts();
        this.error = error;
    }
}
