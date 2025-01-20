package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.account.AccountService;
import org.poo.models.account.BusinessAccount;
import org.poo.models.account.BusinessAssociate;
import org.poo.models.transactions.MoneyTransfer;
import org.poo.models.transactions.Transaction;
import org.poo.models.user.UserDetails;

import java.text.DecimalFormat;

public final class SendMoneyOperation extends MoneyPayments implements TransactionOperation {

    private final BankRepository bankRepository;
    private final CommerciantInput[] commerciants;

    public SendMoneyOperation(final BankRepository bankRepository,
                              final CommerciantInput[] commerciants) {

        this.bankRepository = bankRepository;
        this.commerciants = commerciants;
    }

    private MoneyTransfer moneySentOutput(final CommandInput transferDetails,
                                          final AccountService sender,
                                          final String receiverIban) {

        DecimalFormat df = new DecimalFormat("#.0");
        String money = df.format(transferDetails.getAmount()) + " " + sender.getCurrency();

        return new MoneyTransfer(transferDetails.getTimestamp(),
                transferDetails.getDescription(), sender.getIban(),
                receiverIban, money, "sent");
    }

    private void send(final CommandInput transferDetails, final AccountService sender,
                      final String receiverIban, final UserDetails user, final double moneyInRon,
                      final double commission, final CommerciantInput commerciant) {

        MoneyTransfer moneySent = moneySentOutput(transferDetails, sender, receiverIban);

        sender.setBalance(sender.getBalance() - transferDetails.getAmount() - commission);
        user.getTransactions().add(moneySent);

        BusinessAssociate associate = null;

        if (sender.getAccountType().equals("business")) {

            associate = ((BusinessAccount) sender).findAssociate(user.getUserInput().getEmail());

            if (associate != null) {
                associate.setMoneySpent(associate.getMoneySpent() + transferDetails.getAmount());
            }
        }

        commerciantUpdates(commerciant, associate, sender, user,
                transferDetails.getAmount(), moneyInRon);
        silverToGoldUpgrade(user, moneyInRon, transferDetails.getTimestamp(), sender.getIban());
    }

    private void receive(final CommandInput transferDetails, final AccountService sender,
                         final AccountService receiver, final UserDetails user,
                         final double amount) {

        String money = amount + " " + receiver.getCurrency();

        MoneyTransfer moneyReceived = new MoneyTransfer(transferDetails.getTimestamp(),
                transferDetails.getDescription(), sender.getIban(),
                receiver.getIban(), money, "received");

        receiver.setBalance(receiver.getBalance() + amount);
        user.getTransactions().add(moneyReceived);
    }

    private CommerciantInput findCommerciant(final String iban) {

        for (CommerciantInput c : commerciants) {
            if (c.getAccount().equals(iban)) {

                return c;
            }
        }

        return null;
    }

    @Override
    public String execute(final CommandInput transferDetails, final ExchangeInput[] exchangeRates) {

        if (!transferDetails.getAccount().startsWith("RO")) {
            return null;
        }

        AccountService sender = bankRepository.findAccountByIBAN(transferDetails.getAccount());
        AccountService receiver = bankRepository.findAccountByIBAN(transferDetails.getReceiver());

        if (sender == null) {
            return "User not found";
        }

        CommerciantInput commerciant = findCommerciant(transferDetails.getReceiver());
        double amount;

        if (receiver != null) {
            amount = getAmount(receiver.getCurrency(), transferDetails.getAmount(),
                    sender.getCurrency(), exchangeRates);
        } else if (commerciant != null) {
            amount = transferDetails.getAmount();
        } else {
            return "User not found";
        }

        UserDetails u1 = bankRepository.findUser(transferDetails.getEmail());

        double moneyInRon = getAmount("RON", transferDetails.getAmount(),
                sender.getCurrency(), exchangeRates);

        double commission =
                calculateCommission(u1, transferDetails.getAmount(), moneyInRon, sender);

        if (sender.getBalance() < transferDetails.getAmount() + commission) {

            u1.getTransactions().add(new Transaction(transferDetails.getTimestamp(),
                    "Insufficient funds"));

            return null;
        }

        if (sender.getAccountType().equals("business")) {

            if (((BusinessAccount) sender).isEmployee(u1)
                    && amount > ((BusinessAccount) sender).getSpendingLimit()) {

                return null;
            }
        } else if (bankRepository.findUserByAccount(sender) != u1) {
            return null;
        }

        send(transferDetails, sender, transferDetails.getReceiver(),
                u1, moneyInRon, commission, commerciant);

        if (receiver != null) {
            UserDetails u2 = bankRepository.findUserByAccount(receiver);
            receive(transferDetails, sender, receiver, u2, amount);
        }

        return null;
    }
}
