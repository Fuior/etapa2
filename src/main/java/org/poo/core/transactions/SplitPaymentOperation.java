package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.AccountService;
import org.poo.models.CustomSplitPayment;
import org.poo.models.CustomSplitPaymentError;
import org.poo.models.EqualSplitPayment;
import org.poo.models.EqualSplitPaymentError;
import org.poo.models.SplitPaymentFormat;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class SplitPaymentOperation extends MoneyPayments implements TransactionOperation {

    private final BankRepository bankRepository;
    private final List<CommandInput> splitPayments;
    private final List<CommandInput> splitPaymentsResponses;

    public SplitPaymentOperation(final BankRepository bankRepository,
                                 final List<CommandInput> splitPayments,
                                 final List<CommandInput> splitPaymentsResponses) {

        this.bankRepository = bankRepository;
        this.splitPayments = splitPayments;
        this.splitPaymentsResponses = splitPaymentsResponses;
    }

    private String isEqualPaymentValid(final ArrayList<AccountService> accounts,
                                       final double amount, final String currency,
                                       final ExchangeInput[] exchangeRates) {

        for (AccountService a : accounts) {

            double value = getAmount(a.getCurrency(), amount, currency, exchangeRates);

            if (a.getBalance() < value) {
                return a.getIban();
            }
        }

        return null;
    }

    private String isCustomPaymentValid(final ArrayList<AccountService> accounts,
                                        final List<Double> amount, final String currency,
                                        final ExchangeInput[] exchangeRates) {

        for (int i = 0; i < accounts.size(); i++) {

            double value = getAmount(accounts.get(i).getCurrency(), amount.get(i),
                    currency, exchangeRates);

            if (accounts.get(i).getBalance() < value) {
                return accounts.get(i).getIban();
            }
        }

        return null;
    }

    private void setTransactionError(final ArrayList<AccountService> accounts,
                                     final String description, final String error,
                                     final SplitPaymentFormat payment) {

        for (AccountService a : accounts) {
            UserDetails user = bankRepository.findUserByAccount(a);

            if (payment.getSplitPaymentType().equals("equal")) {
                user.getTransactions()
                        .add(new EqualSplitPaymentError(description, error,
                                (EqualSplitPayment) payment));
            } else {
                user.getTransactions()
                        .add(new CustomSplitPaymentError(description, error,
                                (CustomSplitPayment) payment));
            }

            user.getTransactions().sort(Comparator.comparing(Transaction::getTimestamp));
        }
    }

    private void makeEqualPayment(final ArrayList<AccountService> accounts,
                                  final double valuePerPerson,
                                  final String currency, final String description,
                                  final ExchangeInput[] exchangeRates,
                                  final EqualSplitPayment payment) {

        String iban = isEqualPaymentValid(accounts, valuePerPerson,
                currency, exchangeRates);

        if (iban != null) {
            String error = "Account " + iban + " has insufficient funds for a split payment.";

            setTransactionError(accounts, description, error, payment);
            return;
        }

        for (AccountService a : accounts) {

            double amount = getAmount(a.getCurrency(), valuePerPerson,
                    currency, exchangeRates);

            UserDetails user = bankRepository.findUserByAccount(a);

            a.setBalance(a.getBalance() - amount);
            user.getTransactions().add(payment);
            user.getTransactions().sort(Comparator.comparing(Transaction::getTimestamp));
        }
    }

    private void makeCustomPayment(final ArrayList<AccountService> accounts,
                                   final List<Double> valuePerPerson, final String currency,
                                   final String description, final ExchangeInput[] exchangeRates,
                                   final CustomSplitPayment payment) {

        String iban = isCustomPaymentValid(accounts, valuePerPerson,
                currency, exchangeRates);

        if (iban != null) {
            String error = "Account " + iban + " has insufficient funds for a split payment.";

            setTransactionError(accounts, description, error, payment);
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {

            double amount = getAmount(accounts.get(i).getCurrency(), valuePerPerson.get(i),
                    currency, exchangeRates);

            UserDetails user = bankRepository.findUserByAccount(accounts.get(i));

            accounts.get(i).setBalance(accounts.get(i).getBalance() - amount);
            user.getTransactions().add(payment);
            user.getTransactions().sort(Comparator.comparing(Transaction::getTimestamp));
        }
    }

    private void makeSplitPayment(final CommandInput paymentDetails,
                                  final ExchangeInput[] exchangeRates) {

        ArrayList<AccountService> accounts =
                bankRepository.getAccounts(paymentDetails.getAccounts());
        double valuePerPerson = paymentDetails.getAmount() / paymentDetails.getAccounts().size();

        DecimalFormat df = new DecimalFormat("#.00");
        String money = df.format(paymentDetails.getAmount()) + " " + paymentDetails.getCurrency();
        String description = "Split payment of " + money;

        SplitPaymentFormat payment;

        if (paymentDetails.getSplitPaymentType().equals("equal")) {
            payment = new EqualSplitPayment(paymentDetails, description,
                    valuePerPerson, paymentDetails.getAccounts());
        } else {
            payment = new CustomSplitPayment(paymentDetails, description,
                    paymentDetails.getAccounts());
        }

        for (CommandInput splitPaymentsRespons : splitPaymentsResponses) {

            if (splitPaymentsRespons.getCommand().equals("rejectSplitPayment")) {

                setTransactionError(accounts, description,
                        "One user rejected the payment.", payment);

                return;
            }
        }

        if (paymentDetails.getSplitPaymentType().equals("equal")) {

            makeEqualPayment(accounts, valuePerPerson, paymentDetails.getCurrency(),
                    description, exchangeRates, (EqualSplitPayment) payment);
        } else {

            makeCustomPayment(accounts, paymentDetails.getAmountForUsers(),
                    paymentDetails.getCurrency(), description,
                    exchangeRates, (CustomSplitPayment) payment);
        }
    }

    @Override
    public String execute(final CommandInput paymentDetails,
                          final ExchangeInput[] exchangeRates) {

        if (paymentDetails.getCommand().equals("splitPayment")) {
            splitPayments.add(paymentDetails);
            return null;
        }

        if (bankRepository.findUser(paymentDetails.getEmail()) == null) {
            return "User not found";
        }

        splitPaymentsResponses.add(paymentDetails);

        if (splitPayments.isEmpty()) {
            return null;
        }

        if (splitPaymentsResponses.size() == splitPayments.getFirst().getAccounts().size()) {

            makeSplitPayment(splitPayments.getFirst(), exchangeRates);
            splitPayments.removeFirst();
            splitPaymentsResponses.clear();
        }

        return null;
    }
}
