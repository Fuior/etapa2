package org.poo.models;

import lombok.Getter;
import org.poo.fileio.CommandInput;

@Getter
public class SplitPaymentFormat extends Transaction {

    private final String splitPaymentType;
    private final String currency;

    public SplitPaymentFormat(final CommandInput paymentDetails, final String description) {

        super(paymentDetails.getTimestamp(), description);
        this.splitPaymentType = paymentDetails.getSplitPaymentType();
        this.currency = paymentDetails.getCurrency();
    }
}
