package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;
import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.CardPaymentFormat;
import org.poo.models.ClassicAccount;
import org.poo.models.MoneyTransfer;
import org.poo.models.SplitPaymentError;
import org.poo.models.SplitPaymentFormat;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public final class TransactionService extends BankRepositoryEntity {

    private final CardServiceManager cardServiceManager;

    public TransactionService(final BankRepository bankRepository,
                              final CardServiceManager cardServiceManager) {

        super(bankRepository);
        this.cardServiceManager = cardServiceManager;
    }

    private double getAmount(final String receiverCurrency, final double value,
                             final String senderCurrency, final ExchangeInput[] exchangeRates) {

        if (!senderCurrency.equals(receiverCurrency)) {

            CurrencyExchange currencyExchange = new CurrencyExchange(exchangeRates);
            double rate = currencyExchange.findRate(senderCurrency, receiverCurrency);

            final int scaleBase = 10;
            final int scalePower = 14;
            double scale = Math.pow(scaleBase, scalePower);

            return  ((Math.round((value * rate) * scale)) / scale);
        }

        return value;
    }

    private void pay(final UserDetails user, final AccountService account, final CardDetails card,
                     final CommandInput cardDetails, final double amount) {

        BigDecimal firstValue = BigDecimal.valueOf(account.getBalance());
        BigDecimal secondValue = BigDecimal.valueOf(amount);

        BigDecimal result = firstValue.subtract(secondValue);
        account.setBalance(result.doubleValue());

        CardPaymentFormat cardPayment = new CardPaymentFormat(cardDetails.getTimestamp(),
                "Card payment", amount, cardDetails.getCommerciant());

        user.getTransactions().add(cardPayment);

        if (account.getAccountType().equals("classic")) {
            ((ClassicAccount) account).getCardPayments().add(cardPayment);
        }

        if (card.getType().equals("one time card")) {
            cardServiceManager.replaceCard(user, account, card, cardDetails.getTimestamp());
        }
    }

    /**
     * Aceasta metoda genereaza o plata cu cardul
     *
     * @param cardDetails detaliile platii
     * @param exchangeRates cursurile de schimb valutar
     * @return
     */
    public PayOnlineOutput payOnline(final CommandInput cardDetails,
                                     final ExchangeInput[] exchangeRates) {

        CardDetails card = bankRepository.findCardByNumber(cardDetails.getCardNumber());

        if (card == null) {
            return new PayOnlineOutput(cardDetails.getTimestamp());
        }

        UserDetails user = bankRepository.findUser(cardDetails.getEmail());

        if (card.getCardStatus().equals("frozen")) {

            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "The card is frozen"));

            return null;
        }

        AccountService account = bankRepository.findAccountByCard(card.getCardNumber());
        double amount = getAmount(account.getCurrency(), cardDetails.getAmount(),
                cardDetails.getCurrency(), exchangeRates);

        if (account.getBalance() >= amount) {
            pay(user, account, card, cardDetails, amount);
        } else {
            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "Insufficient funds"));
        }

        return null;
    }

    private void send(final CommandInput transferDetails, final AccountService sender,
                      final AccountService receiver, final UserDetails user) {

        DecimalFormat df = new DecimalFormat("#.0");
        String money = df.format(transferDetails.getAmount()) + " " + sender.getCurrency();

        MoneyTransfer moneySent = new MoneyTransfer(transferDetails.getTimestamp(),
                transferDetails.getDescription(), sender.getIban(),
                receiver.getIban(), money, "sent");

        sender.setBalance(sender.getBalance() - transferDetails.getAmount());
        user.getTransactions().add(moneySent);
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

    /**
     * Aceasta metoda face un transfer bancar
     *
     * @param transferDetails detaliile pentru transferul bancar
     * @param exchangeRates cursurile de schimb valutar
     */
    public void sendMoney(final CommandInput transferDetails, final ExchangeInput[] exchangeRates) {

        if (!transferDetails.getAccount().startsWith("RO")) {
            return;
        }

        AccountService sender = bankRepository.findAccountByIBAN(transferDetails.getAccount());
        AccountService receiver = bankRepository.findAccountByIBAN(transferDetails.getReceiver());

        if (sender == null || receiver == null) {
            return;
        }

        UserDetails u1 = bankRepository.findUserByAccount(sender);
        UserDetails u2 = bankRepository.findUserByAccount(receiver);

        double amount = getAmount(receiver.getCurrency(), transferDetails.getAmount(),
                sender.getCurrency(), exchangeRates);

        if (sender.getBalance() >= transferDetails.getAmount()) {

            send(transferDetails, sender, receiver, u1);
            receive(transferDetails, sender, receiver, u2, amount);

        } else {

            u1.getTransactions().add(new Transaction(transferDetails.getTimestamp(),
                    "Insufficient funds"));
        }
    }

    private ArrayList<AccountService> getAccounts(final List<String> ibans) {

        ArrayList<AccountService> accounts = new ArrayList<>();

        for (String iban : ibans) {
            accounts.add(bankRepository.findAccountByIBAN(iban));
        }

        return accounts;
    }

    private String isPaymentValid(final ArrayList<AccountService> accounts, final double amount,
                                  final String currency, final ExchangeInput[] exchangeRates) {

        String iban = null;

        for (AccountService a : accounts) {

            double value = getAmount(a.getCurrency(), amount, currency, exchangeRates);

            if (a.getBalance() < value) {
                iban =  a.getIban();
            }
        }

        return iban;
    }

    private void setTransactionError(final ArrayList<AccountService> accounts,
                                     final int timestamp, final String description,
                                     final SplitPaymentFormat payment, final String iban) {

        for (AccountService a : accounts) {
            UserDetails user = bankRepository.findUserByAccount(a);
            user.getTransactions()
                    .add(new SplitPaymentError(timestamp, description, payment, iban));
        }
    }

    /**
     * Aceasta functie face o plata distribuita
     *
     * @param paymentDetails detaliile platii
     * @param exchangeRates cursurile de schimb valutar
     */
    public void splitPayment(final CommandInput paymentDetails,
                             final ExchangeInput[] exchangeRates) {

        double valuePerPerson = paymentDetails.getAmount() / paymentDetails.getAccounts().size();
        ArrayList<AccountService> accounts = getAccounts(paymentDetails.getAccounts());

        DecimalFormat df = new DecimalFormat("#.00");
        String money = df.format(paymentDetails.getAmount()) + " " + paymentDetails.getCurrency();
        String description = "Split payment of " + money;

        SplitPaymentFormat payment = new SplitPaymentFormat(paymentDetails.getTimestamp(),
                description, paymentDetails.getCurrency(),
                valuePerPerson, paymentDetails.getAccounts());

        String isValid = isPaymentValid(accounts, valuePerPerson,
                paymentDetails.getCurrency(), exchangeRates);

        if (isValid != null) {

            setTransactionError(accounts, paymentDetails.getTimestamp(),
                                description, payment, isValid);

            return;
        }

        for (AccountService a : accounts) {

            double amount = getAmount(a.getCurrency(), valuePerPerson,
                    paymentDetails.getCurrency(), exchangeRates);

            a.setBalance(a.getBalance() - amount);
            UserDetails user = bankRepository.findUserByAccount(a);
            user.getTransactions().add(payment);
        }
    }
}
