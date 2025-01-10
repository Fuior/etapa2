package org.poo.models;

import lombok.Getter;

import java.util.List;

@Getter
public class SplitPaymentError extends Transaction {

    private String currency;
    private double amount;
    private List<String> involvedAccounts;
    private String error;


    public SplitPaymentError(final int timestamp, final String description,
                             final SplitPaymentFormat paymentInput, final String iban) {

        super(timestamp, description);
        this.currency = paymentInput.getCurrency();
        this.amount = paymentInput.getAmount();
        this.involvedAccounts = paymentInput.getInvolvedAccounts();
        this.error = "Account " + iban + " has insufficient funds for a split payment.";
    }
}
