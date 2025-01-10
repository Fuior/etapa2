package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.utils.Utils;

import java.util.ArrayList;

@Getter
@Setter
public abstract class AccountService {

    protected String iban;
    protected double balance;
    protected double minBalance;
    protected String currency;
    protected String accountType;
    protected int timestamp;
    protected ArrayList<CardDetails> cards;

    public AccountService(final String currency, final String accountType,
                          final int timestamp) {

        this.iban = Utils.generateIBAN();
        this.balance = 0;
        this.minBalance = 0;
        this.currency = currency;
        this.accountType = accountType;
        this.timestamp = timestamp;
        this.cards = new ArrayList<>();
    }
}
