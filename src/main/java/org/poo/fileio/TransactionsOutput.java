package org.poo.fileio;

import lombok.Getter;
import org.poo.core.BankRepository;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Getter
public class TransactionsOutput {

    private final String command = "printTransactions";
    private ArrayList<Transaction> output;
    private int timestamp;

    public TransactionsOutput(final int timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Aceasta metoda seteaza campul "output" cu
     * tranzactiile unui user.
     *
     * @param bankRepository instanta a clasei in care se gaseste
     *                       metoda de cautare a unui user
     * @param email email-ul user-ului
     */
    public void setOutput(final BankRepository bankRepository, final String email) {
        UserDetails user = bankRepository.findUser(email);
        output = user.getTransactions();
    }
}
