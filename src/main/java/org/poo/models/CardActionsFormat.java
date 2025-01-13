package org.poo.models;

import lombok.Getter;

@Getter
public class CardActionsFormat extends Transaction {

    private final String card;
    private final String cardHolder;
    private final String account;

    public CardActionsFormat(final int timestamp, final String description,
                             final String card, final String cardHolder, final String account) {

        super(timestamp, description);
        this.card = card;
        this.cardHolder = cardHolder;
        this.account = account;
    }
}
