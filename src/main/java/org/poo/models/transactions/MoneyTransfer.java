package org.poo.models.transactions;

import lombok.Getter;

@Getter
public class MoneyTransfer extends Transaction {

    private final String senderIBAN;
    private final String receiverIBAN;
    private final String amount;
    private final String transferType;

    public MoneyTransfer(final int timestamp, final String description,
                         final String senderIBAN, final String receiverIBAN,
                         final String amount, final String transferType) {

        super(timestamp, description);
        this.senderIBAN = senderIBAN;
        this.receiverIBAN = receiverIBAN;
        this.amount = amount;
        this.transferType = transferType;
    }
}
