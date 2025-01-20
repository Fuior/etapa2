package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class CustomSplitPaymentError extends SplitPaymentError {

    private final Double[] amountForUsers;
    private final List<String> involvedAccounts;
    private final String error;

    public CustomSplitPaymentError(final String description, final String error,
                                   final CustomSplitPayment paymentInput) {

        super(description, paymentInput);
        this.amountForUsers = paymentInput.getAmountForUsers();
        this.involvedAccounts = paymentInput.getInvolvedAccounts();
        this.error = error;
    }
}
