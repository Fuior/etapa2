package org.poo.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.models.AccountService;
import org.poo.models.CardPaymentFormat;
import org.poo.models.Transaction;

import java.util.ArrayList;
import java.util.Comparator;

@Getter
@Setter
class Commerciant {

    private final String commerciant;
    private double total;

    Commerciant(final String commerciant, final double total) {
        this.commerciant = commerciant;
        this.total = total;
    }
}

@Getter
public final class SpendingTransactions extends TransactionFormat {

    @JsonProperty("commerciants")
    private ArrayList<Commerciant> commerciants;

    public SpendingTransactions(final AccountService account) {
        super(account);
        this.commerciants = new ArrayList<>();
    }

    @Override
    public void getTransactions(final ArrayList<? extends Transaction> transactions,
                                final int startTimestamp, final int endTimestamp) {

        for (Transaction t : transactions) {

            if (t.getTimestamp() < startTimestamp) {
                continue;
            }

            if (t.getTimestamp() > endTimestamp) {
                return;
            }

            this.transactions.add(t);
        }
    }

    private int findCommerciant(final String name) {

        for (int i = 0; i < commerciants.size(); i++) {

            if (commerciants.get(i).getCommerciant().equals(name)) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Aceasta metoda genereaza lista de comercianti
     */
    public void getCommerciants() {

        for (Transaction t : transactions) {

            int pos = findCommerciant(((CardPaymentFormat) t).getCommerciant());

            if (pos == -1) {
                commerciants.add(new Commerciant(((CardPaymentFormat) t).getCommerciant(),
                                ((CardPaymentFormat) t).getAmount()));
            } else {
                commerciants.get(pos).setTotal(commerciants.get(pos).getTotal()
                                                + ((CardPaymentFormat) t).getAmount());
            }
        }

        commerciants.sort(Comparator.comparing(Commerciant::getCommerciant));
    }
}
