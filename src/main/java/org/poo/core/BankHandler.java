package org.poo.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.core.transactions.TransactionService;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.UserDetails;

import java.util.ArrayList;

@Data
public final class BankHandler implements IBankHandler {

    private ArrayList<UserDetails> users;
    private BankRepository bankRepository;
    private AccountServiceManager accountServiceManager;
    private CardServiceManager cardServiceManager;
    private TransactionService transactionService;
    private ReportingService reportingService;
    private ServicePlanManager servicePlanManager;

    public BankHandler(final ArrayList<UserDetails> users, final BankRepository bankRepository,
                       final ExchangeInput[] exchangeRates, final CommerciantInput[] commerciants) {

        this.users = users;
        this.bankRepository = bankRepository;
        this.cardServiceManager = new CardServiceManager(bankRepository);
        this.transactionService = new TransactionService(bankRepository,
                cardServiceManager, exchangeRates, commerciants);
        this.accountServiceManager = new AccountServiceManager(bankRepository,
                transactionService, exchangeRates);
        this.reportingService = new ReportingService(bankRepository);
        this.servicePlanManager = new ServicePlanManager(bankRepository);
    }

    @Override
    public void addAccount(final CommandInput accountDetails,
                           final CommerciantInput[] commerciants) {

        accountServiceManager.add(accountDetails, commerciants);
    }

    @Override
    public String deleteAccount(final CommandInput accountDetails) {
        accountServiceManager.delete(accountDetails);
        return accountServiceManager.getError();
    }

    @Override
    public void addNewBusinessAssociate(final CommandInput associateDetails) {
        accountServiceManager.addNewBusinessAssociate(associateDetails);
    }

    @Override
    public void addFunds(final CommandInput fundsDetails) {
        accountServiceManager.addFunds(fundsDetails);
    }

    @Override
    public void setMinBalance(final CommandInput balanceInput) {
        accountServiceManager.setMinBalance(balanceInput);
    }

    @Override
    public void setAlias(final CommandInput aliasDetails) {
        accountServiceManager.setAlias(aliasDetails);
    }

    @Override
    public int addInterest(final CommandInput interestDetails) {
        return accountServiceManager.addInterest(interestDetails);
    }

    @Override
    public int changeInterestRate(final CommandInput interestDetails) {
        return accountServiceManager.changeInterestRate(interestDetails);
    }

    @Override
    public String changeMoneyLimit(final CommandInput limitDetails) {
        return accountServiceManager.changeMoneyLimit(limitDetails);
    }

    @Override
    public void addCard(final CommandInput cardDetails, final CommerciantInput[] commerciants) {
        cardServiceManager.add(cardDetails, commerciants);
    }

    @Override
    public void deleteCard(final CommandInput cardDetails) {
        cardServiceManager.delete(cardDetails);
    }

    @Override
    public String payOnline(final CommandInput cardDetails) {
        return transactionService.executeTransaction(cardDetails);
    }

    @Override
    public String sendMoney(final CommandInput transferDetails) {
        return transactionService.executeTransaction(transferDetails);
    }

    @Override
    public void splitPayment(final CommandInput paymentDetails) {
        transactionService.executeTransaction(paymentDetails);
    }

    @Override
    public String splitPaymentResponse(final CommandInput response) {
        return transactionService.executeTransaction(response);
    }

    @Override
    public Report generateReport(final CommandInput reportDetails) {
        return reportingService.generateReport(reportDetails);
    }

    @Override
    public void withdrawSavings(final CommandInput withdrawalDetails) {
        transactionService.executeTransaction(withdrawalDetails);
    }

    @Override
    public void upgradePlan(final CommandInput accountDetails,
                            final ExchangeInput[] exchangeRates,
                            final ArrayNode output) {

        servicePlanManager.upgradePlan(accountDetails, accountDetails.getNewPlanType(),
                exchangeRates, output);
    }

    @Override
    public String cashWithdrawal(final CommandInput withdrawalDetails) {
        return transactionService.executeTransaction(withdrawalDetails);
    }
}
