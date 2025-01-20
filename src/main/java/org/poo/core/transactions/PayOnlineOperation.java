package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.core.CardServiceManager;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.AccountService;
import org.poo.models.BusinessAccount;
import org.poo.models.BusinessAssociate;
import org.poo.models.CardDetails;
import org.poo.models.CardPaymentFormat;
import org.poo.models.ClassicAccount;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

public final class PayOnlineOperation extends MoneyPayments implements TransactionOperation {

    private final BankRepository bankRepository;
    private final CardServiceManager cardServiceManager;
    private final CommerciantInput[] commerciants;

    public PayOnlineOperation(final BankRepository bankRepository,
                              final CardServiceManager cardServiceManager,
                              final CommerciantInput[] commerciants) {

        this.bankRepository = bankRepository;
        this.cardServiceManager = cardServiceManager;
        this.commerciants = commerciants;
    }

    private CommerciantInput findCommerciant(final String name) {

        for (CommerciantInput c : commerciants) {
            if (c.getCommerciant().equals(name)) {
                return c;
            }
        }

        return null;
    }

    private void pay(final UserDetails user, final AccountService account, final CardDetails card,
                     final CommandInput cardDetails, final double amount, final double moneyInRon,
                     final double commission) {

        account.setBalance(account.getBalance() - amount - commission);

        CardPaymentFormat cardPayment = new CardPaymentFormat(cardDetails.getTimestamp(),
                "Card payment", amount, cardDetails.getCommerciant());

        user.getTransactions().add(cardPayment);

        if (account.getAccountType().equals("classic")) {
            ((ClassicAccount) account).getCardPayments().add(cardPayment);
        }

        if (card.getType().equals("one time card")) {
            cardServiceManager.replaceCard(user, account, card, cardDetails.getTimestamp());
        }

        CommerciantInput commerciant = findCommerciant(cardDetails.getCommerciant());
        BusinessAssociate associate = null;

        if (account.getAccountType().equals("business")) {

            associate = ((BusinessAccount) account).findAssociate(user.getUserInput().getEmail());

            if (associate != null) {
                associate.setMoneySpent(associate.getMoneySpent() + amount);
            }
        }

        commerciantUpdates(commerciant, associate, account, user, amount, moneyInRon);
        silverToGoldUpgrade(user, moneyInRon, cardDetails.getTimestamp(), account.getIban());
    }

    @Override
    public String execute(final CommandInput cardDetails, final ExchangeInput[] exchangeRates) {

        CardDetails card = bankRepository.findCardByNumber(cardDetails.getCardNumber());

        if (card == null) {
            return "Card not found";
        }

        AccountService account = bankRepository.findAccountByCard(card.getCardNumber());
        UserDetails user = bankRepository.findUser(cardDetails.getEmail());

        if (account.getAccountType().equals("business")) {

            if (((BusinessAccount) account).findAssociate(cardDetails.getEmail()) == null
                    && ((BusinessAccount) account).getOwner() != user) {

                return "Card not found";
            }
        } else if (bankRepository.findUserByAccount(account) != user) {
            return "Card not found";
        }

        if (card.getCardStatus().equals("frozen")) {

            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "The card is frozen"));

            return null;
        }

        double amount = getAmount(account.getCurrency(), cardDetails.getAmount(),
                cardDetails.getCurrency(), exchangeRates);

        if (amount == 0) {
            return null;
        }

        double moneyInRon = getAmount("RON", amount, account.getCurrency(), exchangeRates);
        double commission = calculateCommission(user, amount, moneyInRon, account);

        if (account.getBalance() < amount + commission) {

            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "Insufficient funds"));

            return null;
        }

        if (account.getAccountType().equals("business")) {

            if (((BusinessAccount) account).isEmployee(user)
                    && amount > ((BusinessAccount) account).getSpendingLimit()) {

                return null;
            }
        } else if (card.getCardHolder() != user) {
            return "Card not found";
        }

        pay(user, account, card, cardDetails, amount, moneyInRon, commission);

        return null;
    }
}
