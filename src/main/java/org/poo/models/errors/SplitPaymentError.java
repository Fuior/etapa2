package org.poo.models.errors;

import lombok.Getter;
import org.poo.models.transactions.SplitPaymentFormat;
import org.poo.models.transactions.Transaction;

@Getter
public class SplitPaymentError extends Transaction {

    private final String splitPaymentType;
    private final String currency;

    public SplitPaymentError(final String description, final SplitPaymentFormat paymentInput) {

        super(paymentInput.getTimestamp(), description);
        this.splitPaymentType = paymentInput.getSplitPaymentType();
        this.currency = paymentInput.getCurrency();
    }
}
