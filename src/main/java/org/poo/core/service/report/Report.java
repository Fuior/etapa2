package org.poo.core.service.report;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.models.account.AccountService;
import org.poo.models.account.BusinessAccount;
import org.poo.models.account.ClassicAccount;
import org.poo.models.report.CommerciantBusinessReport;
import org.poo.models.account.CommerciantFormat;
import org.poo.models.transactions.SpendingTransactions;
import org.poo.models.transactions.Transaction;
import org.poo.models.report.TransactionBusinessReport;
import org.poo.models.transactions.TransactionFormat;
import org.poo.models.user.UserDetails;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public class Report {

    private final String command;
    private ReportFormat output;
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

        ArrayList<? extends Transaction> transactions;

        if (command.equals("report")) {

            output = new TransactionFormat(account);
            transactions = user.getTransactions();

        } else if (command.equals("spendingsReport")
                && !account.getAccountType().equals("savings")) {

            output = new SpendingTransactions(account);
            transactions = ((ClassicAccount) account).getCardPayments();

        } else {

            if (!account.getAccountType().equals("business")) {
                return;
            }

            if (commandInput.getType().equals("transaction")) {

                output = new TransactionBusinessReport((BusinessAccount) account,
                        commandInput.getType());

                ((TransactionBusinessReport) output)
                        .addAssociates(((BusinessAccount) account).getManagers(),
                                ((BusinessAccount) account).getEmployees());
            } else {
                List<CommerciantFormat> commerciants =
                        ((BusinessAccount) account).getCommerciants();

                commerciants.sort(Comparator.comparing(CommerciantFormat::getCommerciant));

                output = new CommerciantBusinessReport((BusinessAccount) account,
                        commandInput.getType(), commerciants);
            }

            return;
        }

        ((TransactionFormat) output).getTransactions(transactions, commandInput.getStartTimestamp(),
                commandInput.getEndTimestamp());

        if (command.equals("spendingsReport")) {
            ((SpendingTransactions) output).getCommerciants();
        }
    }
}
