package org.poo.models;

import lombok.Getter;

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
