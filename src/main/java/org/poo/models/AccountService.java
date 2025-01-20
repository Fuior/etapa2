package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
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
    protected ArrayList<CommerciantTransactions> foodTransactions;
    protected ArrayList<CommerciantTransactions> clothesTransactions;
    protected ArrayList<CommerciantTransactions> techTransactions;
    protected double totalSpentByMerchants;

    public AccountService(final CommandInput accountDetails) {

        this.iban = Utils.generateIBAN();
        this.balance = 0;
        this.minBalance = 0;
        this.currency = accountDetails.getCurrency();
        this.accountType = accountDetails.getAccountType();
        this.timestamp = accountDetails.getTimestamp();
        this.cards = new ArrayList<>();
        this.foodTransactions = new ArrayList<>();
        this.clothesTransactions = new ArrayList<>();
        this.techTransactions = new ArrayList<>();
        this.totalSpentByMerchants = 0;
    }

    /**
     * Acesata metoda initializeaza campurile:
     * foodTransactions, clothesTransactions, techTransactions
     *
     * @param commerciants lista de comercianti
     */
    public void setCommerciantTransactions(final CommerciantInput[] commerciants) {

        for (CommerciantInput c : commerciants) {
            if (c.getType().equals("Food")) {
                foodTransactions.add(new CommerciantTransactions(c.getCommerciant()));
            } else if (c.getType().equals("Clothes")) {
                clothesTransactions.add(new CommerciantTransactions(c.getCommerciant()));
            } else {
                techTransactions.add(new CommerciantTransactions(c.getCommerciant()));
            }
        }
    }

    /**
     * Aceasta metoda cauta un comerciant de mancare.
     *
     * @param name numele comerciantului
     * @return comerciantul de mancare cautat sau null daca nu exista
     */
    public CommerciantTransactions getFoodCommerciant(final String name) {

        for (CommerciantTransactions c : foodTransactions) {
            if (c.getName().equals(name)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Aceasta metoda cauta un comerciant de haine.
     *
     * @param name numele comerciantului
     * @return comerciantul de haine cautat sau null daca nu exista
     */
    public CommerciantTransactions getClothesCommerciant(final String name) {

        for (CommerciantTransactions c : clothesTransactions) {
            if (c.getName().equals(name)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Aceasta metoda cauta un comerciant de tech.
     *
     * @param name numele comerciantului
     * @return comerciantul de tech cautat sau null daca nu exista
     */
    public CommerciantTransactions getTechCommerciant(final String name) {

        for (CommerciantTransactions c : techTransactions) {
            if (c.getName().equals(name)) {
                return c;
            }
        }

        return null;
    }
}
