package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.UserInput;

import java.util.ArrayList;

@Getter
@Setter
public class UserDetails {

    private UserInput userInput;
    private ArrayList<AccountService> bankAccounts;
    private Alias alias;
    private ArrayList<Transaction> transactions;
    private String servicePlan;
    private int highValuePaymentCount;

    public record Alias(String name, AccountService account) { }

    public UserDetails(final UserInput userInput) {

        this.userInput = userInput;
        this.bankAccounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.servicePlan = userInput.getOccupation().equals("student") ? "student" : "standard";
        this.highValuePaymentCount = 0;
    }

    /**
     * Aceasta metoda cauta un cont dupa moneda sa.
     *
     * @param currency valuta contului
     * @return contul cautat
     */
    public AccountService findAccountByCurrency(final String currency) {

        for (AccountService a : bankAccounts) {
            if (a.getAccountType().equals("classic")
                    && a.getCurrency().equals(currency)) {

                return a;
            }
        }

        return null;
    }

    /**
     * Aceasta metoda verifica daca un user are un cont classic.
     *
     * @return adevarat daca user-ul are un cont classic si fals in caz contrar
     */
    public boolean hasClassicAccount() {

        for (AccountService a : bankAccounts) {
            if (a.getAccountType().equals("classic")) {
                return true;
            }
        }

        return false;
    }
}
