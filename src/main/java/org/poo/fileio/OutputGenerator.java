package org.poo.fileio;

import lombok.Data;
import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Data
public class OutputGenerator {

    private String command;
    private List<UserOutput> output;
    private int timestamp;

    public OutputGenerator(final String command, final int timestamp) {

        this.command = command;
        this.output = new ArrayList<>();
        this.timestamp = timestamp;
    }

    private ArrayList<CardOutput> createCardsOutput(final AccountService account) {

        ArrayList<CardOutput> cards = new ArrayList<>();

        for (CardDetails card : account.getCards()) {
            cards.add(new CardOutput(card.getCardNumber(), card.getCardStatus()));
        }

        return cards;
    }

    private ArrayList<AccountOutput> createAccountsOutput(final UserDetails user) {

        ArrayList<AccountOutput> accounts = new ArrayList<>();

        for (AccountService account : user.getBankAccounts()) {
            accounts.add(new AccountOutput(account.getIban(),
                        account.getBalance(),
                        account.getCurrency(),
                        account.getAccountType(),
                        createCardsOutput(account)));
        }

        return accounts;
    }

    /**
     * Aceasta metoda genereaza "outputul" pentru
     * afisarea user-ilor in fisierul de iesire.
     *
     * @param users user-ii care for fi afisati
     */
    public void createUsersOutput(final ArrayList<UserDetails> users) {

        for (UserDetails user : users) {

            output.add(new UserOutput(user.getUserInput().getFirstName(),
                        user.getUserInput().getLastName(),
                        user.getUserInput().getEmail(),
                        createAccountsOutput(user)));
        }
    }
}
