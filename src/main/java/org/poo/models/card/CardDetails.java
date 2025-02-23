package org.poo.models.card;

import lombok.Getter;
import lombok.Setter;
import org.poo.models.user.UserDetails;

@Getter
@Setter
public class CardDetails {

    private UserDetails cardHolder;
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
