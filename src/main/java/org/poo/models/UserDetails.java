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

    public record Alias(String name, AccountService account) { }

    public UserDetails(final UserInput userInput) {

        this.userInput = userInput;
        this.bankAccounts = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }
}
