package org.poo.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardDetails {

    private String cardNumber;
    private String type;
    private int timestamp;
    private String cardStatus;

    public CardDetails(final String cardNumber, final String type, final int timestamp) {

        this.cardNumber = cardNumber;
        this.type = type;
        this.timestamp = timestamp;
        this.cardStatus = "active";
    }
}
