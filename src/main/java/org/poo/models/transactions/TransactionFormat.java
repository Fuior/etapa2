package org.poo.models.transactions;

import lombok.Getter;
import org.poo.core.service.report.ReportFormat;
import org.poo.models.account.AccountService;

import java.util.ArrayList;

@Getter
public class TransactionFormat extends ReportFormat {

    protected ArrayList<Transaction> transactions;

    public TransactionFormat(final AccountService account) {

        super(account);
        this.transactions = new ArrayList<>();
    }

    /**
     * Aceasta metoda creeaza o lista cu tranzactiile unui user,
     * fara duplicate, care se afla intr-un interval dat
     *
     * @param transactionsList lista initiala de tranzactii
     * @param startTimestamp timpul de inceput al intervalului
     * @param endTimestamp timpul de sfarsit al intervalului
     */
    public void getTransactions(final ArrayList<? extends Transaction> transactionsList,
                                final int startTimestamp, final int endTimestamp) {

        for (Transaction t : transactionsList) {

            if (t.getTimestamp() < startTimestamp) {
                continue;
            }

            if (t.getTimestamp() > endTimestamp) {
                return;
            }

            if (this.transactions.isEmpty()) {

                this.transactions.add(t);

            } else if (!(this.transactions.getLast().getDescription().equals(t.getDescription())
                        && (this.getTransactions().getLast().getTimestamp() == t.getTimestamp()))) {

                this.transactions.add(t);
            }
        }
    }
}
