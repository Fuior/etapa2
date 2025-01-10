package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.CardActionsFormat;
import org.poo.models.CardDetails;
import org.poo.models.UserDetails;
import org.poo.utils.Utils;

public final class CardServiceManager extends BankRepositoryEntity implements ResourceManager {

    public CardServiceManager(final BankRepository bankRepository) {
        super(bankRepository);
    }

    @Override
    public void add(final CommandInput cardDetails) {

        AccountService account = bankRepository.findAccountByIBAN(cardDetails.getAccount());
        UserDetails user = bankRepository.findUser(cardDetails.getEmail());

        if (account == null || user == null) {
            return;
        }

        String cardNumber = Utils.generateCardNumber();
        String type = cardDetails.getCommand()
                .equals("createCard") ? "basic card" : "one time card";

        account.getCards().add(new CardDetails(cardNumber, type, cardDetails.getTimestamp()));

        bankRepository.addCard(account.getCards().getLast());
        bankRepository.addAccountByCard(account, account.getCards().getLast());

        if (account.getAccountType().equals("classic")) {

            user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                    "New card created", cardNumber,
                    user.getUserInput().getEmail(), account.getIban()));
        }
    }

    @Override
    public void delete(final CommandInput cardDetails) {

        CardDetails card = bankRepository.findCardByNumber(cardDetails.getCardNumber());

        if (card == null) {
            return;
        }

        AccountService account = bankRepository.findAccountByCard(cardDetails.getCardNumber());
        UserDetails user = bankRepository.findUserByAccount(account);

        user.getTransactions().add(new CardActionsFormat(cardDetails.getTimestamp(),
                "The card has been destroyed", card.getCardNumber(),
                user.getUserInput().getEmail(), account.getIban()));

        bankRepository.deleteCard(card.getCardNumber());
        bankRepository.deleteAccountByCard(card);

        account.getCards().removeIf(c -> c.getCardNumber().equals(card.getCardNumber()));
    }

    /**
     * Aceasta metoda sterge un card "one time card"
     * si genereaza unul nou in locul acestuia
     *
     * @param user user-ul care detine cardul
     * @param account contul cu care este asociat cardul
     * @param card datele cardului
     * @param timestamp momentul de timp la care are loc actiunea
     */
    public void replaceCard(final UserDetails user, final AccountService account,
                             final CardDetails card, final int timestamp) {

        user.getTransactions().add(new CardActionsFormat(timestamp,
                "The card has been destroyed", card.getCardNumber(),
                user.getUserInput().getEmail(), account.getIban()));

        bankRepository.deleteCard(card.getCardNumber());
        bankRepository.deleteAccountByCard(card);

        card.setCardNumber(Utils.generateCardNumber());

        bankRepository.addCard(card);
        bankRepository.addAccountByCard(account, card);

        user.getTransactions().add(new CardActionsFormat(timestamp,
                "New card created", card.getCardNumber(),
                user.getUserInput().getEmail(), account.getIban()));
    }
}
