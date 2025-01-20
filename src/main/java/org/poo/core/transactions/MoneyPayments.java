package org.poo.core.transactions;

import org.poo.core.CurrencyExchange;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.AccountService;
import org.poo.models.BusinessAccount;
import org.poo.models.BusinessAssociate;
import org.poo.models.CommerciantTransactions;
import org.poo.models.ServicePlanFormat;
import org.poo.models.UserDetails;

public class MoneyPayments {

    private static final double[][] CASHBACK_RATES = {
            {0.001, 0.003, 0.005},
            {0.002, 0.004, 0.0055},
            {0.0025, 0.005, 0.007}
    };

    private static final int[] MIN_TRANSACTIONS_REQUIRED = {3, 6, 11};
    private static final double[] TRANSACTIONS_CASH_BACK_RATE = {0.02, 0.05, 0.1};

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

    /**
     * Aceasta metoda calculeaza comisionul care trebuie platit la o tranzactie.
     *
     * @param user user-ul care face tranzactia
     * @param amount suma platita
     * @param moneyInRon suma in RON
     * @param account contul din care se transfera bani
     * @return comisionul
     */
    public double calculateCommission(final UserDetails user, final double amount,
                                       final double moneyInRon, final AccountService account) {

        final double minPaymentForCommission = 500;
        String servicePlan;

        if (account.getAccountType().equals("business")) {
            servicePlan = ((BusinessAccount) account).getOwner().getServicePlan();
        } else {
            servicePlan = user.getServicePlan();
        }

        if (servicePlan.equals("standard")) {
            final double rate = 0.002;
            return rate * amount;
        } else if (servicePlan.equals("silver")
                && moneyInRon >= minPaymentForCommission) {

            final double rate = 0.001;
            return rate * amount;
        }

        return 0.0;
    }

    private double getCashBackForNrOfTransactions(final CommerciantInput commerciant,
                                                  final AccountService account) {

        if (commerciant.getType().equals("Food")) {

            CommerciantTransactions foodCommerciant =
                    account.getFoodCommerciant(commerciant.getCommerciant());

            if (foodCommerciant.getNumberOfTransactions() == MIN_TRANSACTIONS_REQUIRED[0]) {
                return TRANSACTIONS_CASH_BACK_RATE[0];
            }
        } else if (commerciant.getType().equals("Clothes")) {

            CommerciantTransactions clothesCommerciant =
                    account.getClothesCommerciant(commerciant.getCommerciant());

            if (clothesCommerciant.getNumberOfTransactions() == MIN_TRANSACTIONS_REQUIRED[1]) {
                return TRANSACTIONS_CASH_BACK_RATE[1];
            }
        } else {

            CommerciantTransactions techCommerciant =
                    account.getTechCommerciant(commerciant.getCommerciant());

            if (techCommerciant.getNumberOfTransactions() == MIN_TRANSACTIONS_REQUIRED[2]) {
                return TRANSACTIONS_CASH_BACK_RATE[2];
            }
        }

        return 0.0;
    }

    private double spendingThresholdCashBack(final UserDetails user,
                                             final AccountService account) {

        final double smallSpendings = 100;
        final double goodSpendings = 300;
        final double bigSpendings = 500;

        double amount = account.getTotalSpentByMerchants();
        String servicePlan;

        if (account.getAccountType().equals("business")) {
            servicePlan = ((BusinessAccount) account).getOwner().getServicePlan();
        } else {
            servicePlan = user.getServicePlan();
        }

        if (amount >= bigSpendings) {

            return (servicePlan.equals("gold")
                    ? CASHBACK_RATES[2][2] : (servicePlan.equals("silver")
                    ? CASHBACK_RATES[2][1] : CASHBACK_RATES[2][0]));
        } else if (amount >= goodSpendings) {

            return (servicePlan.equals("gold")
                    ? CASHBACK_RATES[1][2] : (servicePlan.equals("silver")
                    ? CASHBACK_RATES[1][1] : CASHBACK_RATES[1][0]));
        } else if (amount >= smallSpendings) {

            return (servicePlan.equals("gold")
                    ? CASHBACK_RATES[0][2] : (servicePlan.equals("silver")
                    ? CASHBACK_RATES[0][1] : CASHBACK_RATES[0][0]));
        }

        return 0.0;
    }

    /**
     * Aceasta metoda calculeaza cashback-ul primit de la comerciant.
     *
     * @param commerciant datele comerciantului
     * @param user user-ul care detine contul
     * @param account contul pentru care se genereaza cashback
     * @param amount suma cheltuita
     */
    public void getCashBack(final CommerciantInput commerciant, final UserDetails user,
                               final AccountService account, final double amount) {

        String cashBackPlan = commerciant.getCashbackStrategy();
        double cashBack;

        if (cashBackPlan.equals("nrOfTransactions")) {
            cashBack = getCashBackForNrOfTransactions(commerciant, account) * amount;
        } else {
            cashBack = spendingThresholdCashBack(user, account) * amount;
        }

        account.setBalance(account.getBalance() + cashBack);
    }

    /**
     * Aceasta metoda contorizeaza numarul de tranzactii
     * al unui cont la un tip de comerciant.
     *
     * @param commerciant datele comerciantului
     * @param account datele contului
     */
    public void addCommerciantTransaction(final CommerciantInput commerciant,
                                           final AccountService account) {

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

    /**
     * Aceasta metoda face upgrade automat de la planul silver la cel gold.
     *
     * @param user user-ul pentru care se face upgrade
     * @param moneyInRon ultima suma cheltuita de user in RON
     * @param timestamp momentul de timp la care se face upgrade-ul
     * @param iban IBAN-ul contului user-ului
     */
    public void silverToGoldUpgrade(final UserDetails user, final double moneyInRon,
                                    final int timestamp, final String iban) {

        final double minPaymentForUpgrade = 300;

        if (user.getServicePlan().equals("silver") && moneyInRon >= minPaymentForUpgrade) {
            user.setHighValuePaymentCount(user.getHighValuePaymentCount() + 1);

            final int transactionsForUpgrade = 5;

            if (user.getHighValuePaymentCount() >= transactionsForUpgrade) {
                user.setServicePlan("gold");

                user.getTransactions().add(new ServicePlanFormat(timestamp,
                        "Upgrade plan", iban, "gold"));
            }
        }
    }

    public void commerciantUpdates(final CommerciantInput commerciant, final BusinessAssociate associate,
                                   final AccountService account, final UserDetails user,
                                   final double amount, final double moneyInRon) {

        if (commerciant != null) {

            if (commerciant.getCashbackStrategy().equals("spendingThreshold")) {
                account.setTotalSpentByMerchants(account.getTotalSpentByMerchants() + moneyInRon);
            }

            if (associate != null) {
                ((BusinessAccount) account).addCommerciant(associate,
                        commerciant.getCommerciant(), amount);
            }

            addCommerciantTransaction(commerciant, account);
            getCashBack(commerciant, user, account, amount);
        }
    }
}
