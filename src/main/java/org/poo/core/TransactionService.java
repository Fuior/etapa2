package org.poo.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ErrorOutput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.SimpleOutput;
import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.CardPaymentFormat;
import org.poo.models.CashWithdrawalFormat;
import org.poo.models.ClassicAccount;
import org.poo.models.CommerciantTransactions;
import org.poo.models.CustomSplitPayment;
import org.poo.models.CustomSplitPaymentError;
import org.poo.models.EqualSplitPayment;
import org.poo.models.EqualSplitPaymentError;
import org.poo.models.MoneyTransfer;
import org.poo.models.SavingsWithdrawalFormat;
import org.poo.models.SplitPaymentFormat;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class TransactionService extends BankRepositoryEntity {

    private final CardServiceManager cardServiceManager;
    private final List<CommandInput> splitPayments;
    private final List<CommandInput> splitPaymentsResponses;

    public TransactionService(final BankRepository bankRepository,
                              final CardServiceManager cardServiceManager) {

        super(bankRepository);
        this.cardServiceManager = cardServiceManager;
        this.splitPayments = new ArrayList<>();
        this.splitPaymentsResponses = new ArrayList<>();
    }

    /**
     * Aceasta metoda schimba o valoare dintr-un curs valutar
     * in alt curs valutar.
     *
     * @param receiverCurrency cursul valutar dorit
     * @param value suma care va fi schimbata
     * @param senderCurrency cursul valutar actual
     * @param exchangeRates cursurile de schimb valutar
     * @return suma in noul curs valutar
     */
    public double getAmount(final String receiverCurrency, final double value,
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

    private double calculateCommission(final UserDetails user, final double amount,
                                       final double moneyInRon) {

        final double minPaymentForCommission = 500;

        if (user.getServicePlan().equals("standard")) {
            final double rate = 0.002;
            return rate * amount;
        } else if (user.getServicePlan().equals("silver")
                && moneyInRon >= minPaymentForCommission) {

            final double rate = 0.001;
            return rate * amount;
        }

        return 0.0;
    }

    private void pay(final UserDetails user, final AccountService account, final CardDetails card,
                     final CommandInput cardDetails, final double amount, final double moneyInRon) {

        double commission = calculateCommission(user, amount, moneyInRon);
        account.setBalance(account.getBalance() - amount - commission);
        final double minPaymentForUpgrade = 300;

        if (user.getServicePlan().equals("silver") && moneyInRon >= minPaymentForUpgrade) {
            user.setHighValuePaymentCount(user.getHighValuePaymentCount() + 1);

            final int transactionsForUpgrade = 5;

            if (user.getHighValuePaymentCount() >= transactionsForUpgrade) {
                user.setServicePlan("gold");
            }
        }

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

    private double getCashBack(final double amount, final CommerciantInput commerciant,
                               final String userPlan, final AccountService account) {

        String cashBackPlan = commerciant.getCashbackStrategy();

        if (cashBackPlan.equals("nrOfTransactions")) {

            if (commerciant.getType().equals("Food")) {
                CommerciantTransactions foodCommerciant =
                        account.getFoodCommerciant(commerciant.getCommerciant());

                final int minTransactionsForFood = 3;
                final double cashBackRateForFood = 0.02;

                if (foodCommerciant.getNumberOfTransactions() == minTransactionsForFood) {
                    return cashBackRateForFood;
                }
            } else if (commerciant.getType().equals("Clothes")) {
                CommerciantTransactions clothesCommerciant =
                        account.getClothesCommerciant(commerciant.getCommerciant());

                final int minTransactionsForClothes = 6;
                final double cashBackRateForClothes = 0.05;

                if (clothesCommerciant.getNumberOfTransactions() == minTransactionsForClothes) {
                    return cashBackRateForClothes;
                }
            } else {
                CommerciantTransactions techCommerciant =
                        account.getTechCommerciant(commerciant.getCommerciant());

                final int minTransactionsForTech = 11;
                final double cashBackRateForTech = 0.1;

                if (techCommerciant.getNumberOfTransactions() == minTransactionsForTech) {
                    return cashBackRateForTech;
                }
            }
        } else if (cashBackPlan.equals("spendingThreshold")) {

            final double smallSpendings = 100;
            final double goodSpendings = 300;
            final double bigSpendings = 500;

            if (amount >= bigSpendings) {

                final double standardCashBackRate = 0.0025;
                final double silverCashBackRate = 0.005;
                final double goldCashBackRate = 0.007;

                return (userPlan.equals("gold")
                        ? goldCashBackRate : (userPlan.equals("silver")
                        ? silverCashBackRate : standardCashBackRate));
            } else if (amount >= goodSpendings) {

                final double standardCashBackRate = 0.002;
                final double silverCashBackRate = 0.004;
                final double goldCashBackRate = 0.0055;

                return (userPlan.equals("gold")
                        ? goldCashBackRate : (userPlan.equals("silver")
                        ? silverCashBackRate : standardCashBackRate));
            } else if (amount >= smallSpendings) {

                final double standardCashBackRate = 0.001;
                final double silverCashBackRate = 0.003;
                final double goldCashBackRate = 0.005;

                return (userPlan.equals("gold")
                        ? goldCashBackRate : (userPlan.equals("silver")
                        ? silverCashBackRate : standardCashBackRate));
            }
        }

        return 0.0;
    }

    private CommerciantInput findCommerciant(final CommerciantInput[] commerciants,
                                             final String name) {

        for (CommerciantInput c : commerciants) {
            if (c.getCommerciant().equals(name)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Aceasta metoda genereaza o plata cu cardul
     *
     * @param cardDetails detaliile platii
     * @param exchangeRates cursurile de schimb valutar
     * @return mesajul de eroare sau null daca plata s-a efectuat cu succes
     */
    public ErrorOutput payOnline(final CommandInput cardDetails,
                                 final ExchangeInput[] exchangeRates,
                                 final CommerciantInput[] commerciants) {

        CardDetails card = bankRepository.findCardByNumber(cardDetails.getCardNumber());

        if (card == null) {
            return new ErrorOutput(cardDetails, "Card not found");
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

        if (amount == 0) {
            return null;
        }

        if (account.getBalance() >= amount) {
            double moneyInRon = getAmount("RON", amount, account.getCurrency(), exchangeRates);

            pay(user, account, card, cardDetails, amount, moneyInRon);

            CommerciantInput commerciant =
                    findCommerciant(commerciants, cardDetails.getCommerciant());
            double cashBack = 0.0;

            if (commerciant != null) {
                cashBack = getCashBack(moneyInRon, commerciant, user.getServicePlan(), account)
                        * moneyInRon;

                if (commerciant.getType().equals("Food")) {
                    CommerciantTransactions foodCommerciant =
                            account.getFoodCommerciant(commerciant.getCommerciant());

                    foodCommerciant.setNumberOfTransactions(foodCommerciant
                            .getNumberOfTransactions() + 1);
                } else if (commerciant.getType().equals("Clothes")) {
                    CommerciantTransactions clothesCommerciant =
                            account.getClothesCommerciant(commerciant.getCommerciant());

                    clothesCommerciant.setNumberOfTransactions(clothesCommerciant
                            .getNumberOfTransactions() + 1);
                } else {
                    CommerciantTransactions techCommerciant =
                            account.getTechCommerciant(commerciant.getCommerciant());

                    techCommerciant.setNumberOfTransactions(techCommerciant
                            .getNumberOfTransactions() + 1);
                }
            }

            if (cashBack != 0.0) {
                amount = getAmount(account.getCurrency(), cashBack, "RON", exchangeRates);
                account.setBalance(account.getBalance() + amount);
            }
        } else {
            user.getTransactions().add(new Transaction(cardDetails.getTimestamp(),
                    "Insufficient funds"));
        }

        return null;
    }

    private void send(final CommandInput transferDetails, final AccountService sender,
                      final AccountService receiver, final UserDetails user,
                      final double moneyInRon, final double commission) {

        DecimalFormat df = new DecimalFormat("#.0");
        String money = df.format(transferDetails.getAmount()) + " " + sender.getCurrency();

        MoneyTransfer moneySent = new MoneyTransfer(transferDetails.getTimestamp(),
                transferDetails.getDescription(), sender.getIban(),
                receiver.getIban(), money, "sent");

        sender.setBalance(sender.getBalance() - transferDetails.getAmount() - commission);
        user.getTransactions().add(moneySent);

        final double minPaymentForUpgrade = 300;

        if (user.getServicePlan().equals("silver") && moneyInRon >= minPaymentForUpgrade) {
            user.setHighValuePaymentCount(user.getHighValuePaymentCount() + 1);

            final int transactionsForUpgrade = 5;

            if (user.getHighValuePaymentCount() >= transactionsForUpgrade) {
                user.setServicePlan("gold");
            }
        }
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
    public void sendMoney(final CommandInput transferDetails, final ExchangeInput[] exchangeRates,
                          final ArrayNode output) {

        if (!transferDetails.getAccount().startsWith("RO")) {
            return;
        }

        AccountService sender = bankRepository.findAccountByIBAN(transferDetails.getAccount());
        AccountService receiver = bankRepository.findAccountByIBAN(transferDetails.getReceiver());

        if (sender == null || receiver == null) {

            SimpleOutput myOutput = new SimpleOutput(transferDetails, "User not found");
            ObjectMapper objectMapper = new ObjectMapper();
            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        UserDetails u1 = bankRepository.findUser(transferDetails.getEmail());
        UserDetails u2 = bankRepository.findUserByAccount(receiver);

        double amount = getAmount(receiver.getCurrency(), transferDetails.getAmount(),
                sender.getCurrency(), exchangeRates);

        double moneyInRon = getAmount("RON", transferDetails.getAmount(),
                sender.getCurrency(), exchangeRates);

        double commission = calculateCommission(u1, transferDetails.getAmount(), moneyInRon);

        if (sender.getBalance() >= transferDetails.getAmount() + commission) {

            send(transferDetails, sender, receiver, u1, moneyInRon, commission);
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
                                     final String description, final String iban,
                                     final SplitPaymentFormat payment) {

        for (AccountService a : accounts) {
            UserDetails user = bankRepository.findUserByAccount(a);

            if (payment.getSplitPaymentType().equals("equal")) {
                user.getTransactions()
                        .add(new EqualSplitPaymentError(description, iban,
                                (EqualSplitPayment) payment));
            } else {
                user.getTransactions()
                        .add(new CustomSplitPaymentError(description, iban,
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

        String isValid = isEqualPaymentValid(accounts, valuePerPerson,
                currency, exchangeRates);

        if (isValid != null) {
            setTransactionError(accounts, description, isValid, payment);
            return;
        }

        for (AccountService a : accounts) {

            double amount = getAmount(a.getCurrency(), valuePerPerson,
                    currency, exchangeRates);

            double amountInRon = getAmount("RON", valuePerPerson,
                    currency, exchangeRates);

            UserDetails user = bankRepository.findUserByAccount(a);

            double commission = calculateCommission(user, amount, amountInRon);
            a.setBalance(a.getBalance() - amount - commission);

            user.getTransactions().add(payment);
            user.getTransactions().sort(Comparator.comparing(Transaction::getTimestamp));
        }
    }

    private void makeCustomPayment(final ArrayList<AccountService> accounts,
                                   final List<Double> valuePerPerson, final String currency,
                                   final String description, final ExchangeInput[] exchangeRates,
                                   final CustomSplitPayment payment) {

        String isValid = isCustomPaymentValid(accounts, valuePerPerson,
                currency, exchangeRates);

        if (isValid != null) {
            setTransactionError(accounts, description, isValid, payment);
            return;
        }

        for (int i = 0; i < accounts.size(); i++) {

            double amount = getAmount(accounts.get(i).getCurrency(), valuePerPerson.get(i),
                    currency, exchangeRates);

            double amountInRon = getAmount("RON", valuePerPerson.get(i),
                    currency, exchangeRates);

            UserDetails user = bankRepository.findUserByAccount(accounts.get(i));

            double commission = calculateCommission(user, amount, amountInRon);
            accounts.get(i).setBalance(accounts.get(i).getBalance() - amount - commission);

            user.getTransactions().add(payment);
            user.getTransactions().sort(Comparator.comparing(Transaction::getTimestamp));
        }
    }

    /**
     * Aceasta functie face o plata distribuita
     *
     * @param paymentDetails detaliile platii
     */
    public void splitPayment(final CommandInput paymentDetails) {
        splitPayments.add(paymentDetails);
    }

    private void makeSplitPayment(final CommandInput paymentDetails,
                             final ExchangeInput[] exchangeRates) {

        ArrayList<AccountService> accounts = getAccounts(paymentDetails.getAccounts());
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

        for (int i = 0; i < splitPaymentsResponses.size(); i++) {

            if (splitPaymentsResponses.get(i).getCommand().equals("rejectSplitPayment")) {

                setTransactionError(accounts, "One user rejected the payment.",
                        accounts.get(i).getIban(), payment);

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

    /**
     * Aceasta metoda verifica daca persoanele implicate intr-o plata distribuita
     * au acceptat sau au refuzat plata.
     *
     * @param output contine mesajele care se afiseaza in fisierul de output
     * @param response raspunsul unui user
     * @param exchangeRates cursurile de schimb valutar
     */
    public void splitPaymentResponse(final ArrayNode output, final CommandInput response,
                                     final ExchangeInput[] exchangeRates) {

        if (bankRepository.findUser(response.getEmail()) == null) {

            SimpleOutput myOutput = new SimpleOutput(response, "User not found");
            ObjectMapper objectMapper = new ObjectMapper();
            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        splitPaymentsResponses.add(response);

        if (splitPayments.isEmpty()) {
            return;
        }

        if (splitPaymentsResponses.size() == splitPayments.getFirst().getAccounts().size()) {

            makeSplitPayment(splitPayments.getFirst(), exchangeRates);
            splitPayments.removeFirst();
            splitPaymentsResponses.clear();
        }
    }

    private boolean isClassicAccount(final ArrayList<AccountService> bankAccounts) {

        for (AccountService a : bankAccounts) {
            if (a.getAccountType().equals("classic")) {
                return true;
            }
        }

        return false;
    }

    private AccountService findAccountByCurrency(final ArrayList<AccountService> bankAccounts,
                                                 final String currency) {

        for (AccountService a : bankAccounts) {
            if (a.getAccountType().equals("classic")
                && a.getCurrency().equals(currency)) {

                return a;
            }
        }

        return null;
    }

    /**
     * Aceasta metoda pune banii dintr-un cont de economii
     * intr-un cont classic.
     *
     * @param withdrawalDetails datele necesare tranzactiei
     * @param exchangeRates cursurile de schimb valutar
     */
    public void withdrawSavings(final CommandInput withdrawalDetails,
                                final ExchangeInput[] exchangeRates) {

        AccountService account = bankRepository.findAccountByIBAN(withdrawalDetails.getAccount());
        UserDetails user = bankRepository.findUserByAccount(account);

        AccountService receiver = findAccountByCurrency(user.getBankAccounts(),
                withdrawalDetails.getCurrency());

        LocalDate birthDate = LocalDate.parse(user.getUserInput().getBirthDate());
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        double amount = getAmount(account.getCurrency(), withdrawalDetails.getAmount(),
                withdrawalDetails.getCurrency(), exchangeRates);

        String message;
        final int minAgeForTransaction = 21;

        if (age < minAgeForTransaction) {
            message = "You don't have the minimum age required.";
        } else if (!isClassicAccount(user.getBankAccounts())) {
            message = "You do not have a classic account.";
        } else if (account.getAccountType().equals("classic")) {
            message = "Account is not of type savings.";
        } else if (amount > account.getBalance()) {
            message = "Insufficient funds";
        } else if (receiver == null) {
            message = "Account not found";
        } else {
            message = "Savings withdrawal";
        }

        if (message.equals("Savings withdrawal")) {

            account.setBalance(account.getBalance() - amount);
            receiver.setBalance(receiver.getBalance() + withdrawalDetails.getAmount());

            user.getTransactions()
                    .add(new SavingsWithdrawalFormat(withdrawalDetails, receiver.getIban()));
            user.getTransactions()
                    .add(new SavingsWithdrawalFormat(withdrawalDetails, receiver.getIban()));
        } else {
            user.getTransactions().add(new Transaction(withdrawalDetails.getTimestamp(), message));
        }
    }

    /**
     * Aceasta metoda retrage bani numerar dintr-un cont
     *
     * @param withdrawalDetails datele necasare retragerii
     * @param exchangeRates cursurile de schimb valutar
     * @return mesajul de eroare al tranzactiei
     */
    public String cashWithdrawal(final CommandInput withdrawalDetails,
                               final ExchangeInput[] exchangeRates) {

        CardDetails card = bankRepository.findCardByNumber(withdrawalDetails.getCardNumber());

        if (card == null) {
            return "Card not found";
        }

        UserDetails user = bankRepository.findUser(withdrawalDetails.getEmail());

        if (user == null) {
            return "User not found";
        }

        AccountService account =
                bankRepository.findAccountByCard(withdrawalDetails.getCardNumber());
        double amount = getAmount(account.getCurrency(), withdrawalDetails.getAmount(),
                "RON", exchangeRates);

        if (amount > account.getBalance()) {
            user.getTransactions().add(new Transaction(withdrawalDetails.getTimestamp(),
                    "Insufficient funds"));

            return null;
        }

        if (card.getType().equals("one time card") && card.getCardStatus().equals("frozen")) {
            return "Card has already been used";
        } else if (card.getCardStatus().equals("frozen")) {
            return "The card is frozen";
        }

        double commission = calculateCommission(user, amount, withdrawalDetails.getAmount());
        double withdrawalMoney = account.getBalance() - amount - commission;

        if (account.getMinBalance() != 0.0 && withdrawalMoney < account.getMinBalance()) {
            return "Cannot perform payment due to a minimum balance being set";
        }

        account.setBalance(withdrawalMoney);
        user.getTransactions().add(new CashWithdrawalFormat(withdrawalDetails.getTimestamp(),
                "Cash withdrawal of " + withdrawalDetails.getAmount(),
                withdrawalDetails.getAmount()));

        return null;
    }
}
