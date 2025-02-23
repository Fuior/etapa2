package org.poo.core.service.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.core.BankRepository;
import org.poo.core.BankRepositoryEntity;
import org.poo.core.transactions.MoneyPayments;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.SimpleOutput;
import org.poo.models.account.AccountService;
import org.poo.models.user.ServicePlanFormat;
import org.poo.models.transactions.Transaction;
import org.poo.models.user.UserDetails;

public final class ServicePlanManager extends BankRepositoryEntity {

    private final MoneyPayments moneyPayments;

    public ServicePlanManager(final BankRepository bankRepository) {

        super(bankRepository);
        this.moneyPayments = new MoneyPayments();
    }

    private String payFee(final AccountService account, final double fee,
                         final ExchangeInput[] exchangeRates) {

        double amount = moneyPayments.getAmount(account.getCurrency(),
                fee, "RON", exchangeRates);

        if (account.getBalance() < amount) {
            return "Insufficient funds";
        }

        account.setBalance(account.getBalance() - amount);

        return "Upgrade plan";
    }

    /**
     * Aceasta metoda face upgrade pentru planul unui user
     *
     * @param accountDetails datele contului detinut de user
     * @param newPlanType noul plan
     * @param exchangeRates cursurile de schimb valutar
     * @param output contine mesajele ce vor fi afisate in fisierul de output
     */
    public void upgradePlan(final CommandInput accountDetails, final String newPlanType,
                            final ExchangeInput[] exchangeRates, final ArrayNode output) {

        AccountService account = bankRepository.findAccountByIBAN(accountDetails.getAccount());

        if (account == null) {

            SimpleOutput myOutput = new SimpleOutput(accountDetails, "Account not found");
            ObjectMapper objectMapper = new ObjectMapper();
            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        UserDetails user = bankRepository.findUserByAccount(account);
        String userPlan = user.getServicePlan();

        if (userPlan.equals(newPlanType)) {

            user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                    "The user already has the " + newPlanType + " plan."));

            return;
        } else if (userPlan.equals("gold")
                || (userPlan.equals("silver") && !newPlanType.equals("gold"))) {

            user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                    "You cannot downgrade your plan."));

            return;
        } else if ((userPlan.equals("standard") && newPlanType.equals("student"))
                || (userPlan.equals("student") && newPlanType.equals("standard"))) {

            return;
        }

        final double standardToSilverFee = 100;
        final double silverToGoldFee = 250;
        final double standardToGoldFee = 350;

        double fee = userPlan.equals("silver")
                ? silverToGoldFee : (newPlanType.equals("silver")
                ? standardToSilverFee : standardToGoldFee);

        String message = payFee(account, fee, exchangeRates);

        if (message.equals("Upgrade plan")) {
            user.setServicePlan(newPlanType);

            user.getTransactions().add(new ServicePlanFormat(accountDetails.getTimestamp(),
                    message, accountDetails.getAccount(), accountDetails.getNewPlanType()));
        } else {
            user.getTransactions().add(new Transaction(accountDetails.getTimestamp(), message));
        }
    }
}
