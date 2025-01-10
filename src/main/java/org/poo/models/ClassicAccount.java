package org.poo.models;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ClassicAccount extends  AccountService {

    private ArrayList<CardPaymentFormat> cardPayments;

    public ClassicAccount(final String currency, final String accountType,
                          final int timestamp) {

        super(currency, accountType, timestamp);
        this.cardPayments = new ArrayList<>();
    }
}
