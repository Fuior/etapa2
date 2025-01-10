package org.poo.core;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.ClassicAccount;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Getter
public class Report {

    private final String command;
    private TransactionFormat output;
    private final int timestamp;

    public Report(final String command, final int timestamp) {
        this.command = command;
        this.timestamp = timestamp;
    }

    /**
     * Aceasta metoda seteaza campul "output" cu informatiile
     * tranzactiilor unui cont.
     *
     * @param commandInput datele actiunii
     * @param user user-ul care detine contul
     * @param account contul pentru care se face raportul
     */
    public void setOutput(final CommandInput commandInput, final UserDetails user,
                          final AccountService account) {

        if (account.getAccountType().equals("savings")) {
            this.output = null;
            return;
        }

        ArrayList<? extends Transaction> transactions;

        if (commandInput.getCommand().equals("report")) {
            this.output = new TransactionFormat(account);
            transactions = user.getTransactions();
        } else {
            this.output = new SpendingTransactions(account);
            transactions = ((ClassicAccount) account).getCardPayments();
        }

        output.getTransactions(transactions, commandInput.getStartTimestamp(),
                                commandInput.getEndTimestamp());

        if (commandInput.getCommand().equals("spendingsReport")) {
            ((SpendingTransactions) output).getCommerciants();
        }
    }
}
