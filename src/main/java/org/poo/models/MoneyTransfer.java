package org.poo.models;

import lombok.Getter;

@Getter
public class MoneyTransfer extends Transaction {

    private String senderIBAN;
    private String receiverIBAN;
    private String amount;
    private String transferType;

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
