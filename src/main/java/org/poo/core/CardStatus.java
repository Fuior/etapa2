package org.poo.core;

import lombok.Getter;
import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public final class CardStatus {

    private final String command = "checkCardStatus";
    private Transaction output;
    private final int timestamp;

    public CardStatus(final int timestamp) {
        this.timestamp = timestamp;
    }

    private CardDetails findCard(final String cardNumber, final AccountService account) {

        for (CardDetails card : account.getCards()) {

            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }

        return null;
    }

    private void setError(final AtomicInteger error, final AccountService account,
                          final CardDetails card) {

        final double balanceWarningMargin = 30;

        if ((account.getBalance() <= account.getMinBalance())
                || (account.getBalance() - account.getMinBalance() <= balanceWarningMargin)
                || (card.getCardStatus().equals("frozen"))
                || (account.getMinBalance() == 0)) {

            error.set(-1);
        }
    }

    private CardDetails findCard(final String cardNumber, final ArrayList<UserDetails> users,
                                 final AtomicInteger error, final int time) {

        for (UserDetails user : users) {

            for (AccountService account : user.getBankAccounts()) {

                CardDetails card = findCard(cardNumber, account);

                if (card == null) {
                    continue;
                }

                if (account.getBalance() <= account.getMinBalance()) {
                    card.setCardStatus("frozen");

                    user.getTransactions().add(new Transaction(time,
                            "You have reached the minimum amount of funds,"
                                    + " the card will be frozen"));
                }

                setError(error, account, card);

                return card;

            }
        }

        return null;
    }

    /**
     * Aceasta metoda verifica statusul unui card.
     * Daca este "activ", actiunea se va face cu succes,
     * altfel se va genera o eroare.
     *
     * @param time timpul la care are loc actiunea
     * @param cardNumber numarul cardului
     * @param users user-ul care detine cardul
     * @return eroarea generata sau "0" pentru succes
     */
    public int checkStatus(final int time, final String cardNumber,
                           final ArrayList<UserDetails> users) {

        AtomicInteger error = new AtomicInteger(0);
        CardDetails card = findCard(cardNumber, users, error, time);

        if (card == null) {
            output = new Transaction(time, "Card not found");
        } else {
            output = new Transaction(time, card.getCardStatus());
        }

        return error.get();
    }
}
