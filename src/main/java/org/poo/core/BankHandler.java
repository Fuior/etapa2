package org.poo.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.ErrorOutput;
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

    public BankHandler(final ArrayList<UserDetails> users, final BankRepository bankRepository) {

        this.users = users;
        this.bankRepository = bankRepository;
        this.accountServiceManager = new AccountServiceManager(bankRepository);
        this.cardServiceManager = new CardServiceManager(bankRepository);
        this.transactionService = new TransactionService(bankRepository, cardServiceManager);
        this.reportingService = new ReportingService(bankRepository);
        this.servicePlanManager = new ServicePlanManager(bankRepository, transactionService);
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
    public void addCard(final CommandInput cardDetails, final CommerciantInput[] commerciants) {
        cardServiceManager.add(cardDetails, commerciants);
    }

    @Override
    public void deleteCard(final CommandInput cardDetails) {
        cardServiceManager.delete(cardDetails);
    }

    @Override
    public ErrorOutput payOnline(final CommandInput cardDetails,
                                 final ExchangeInput[] exchangeRates,
                                 final CommerciantInput[] commerciants) {

        return transactionService.payOnline(cardDetails, exchangeRates, commerciants);
    }

    @Override
    public void sendMoney(final CommandInput transferDetails, final ArrayNode output,
                          final ExchangeInput[] exchangeRates) {

        transactionService.sendMoney(transferDetails, exchangeRates, output);
    }

    @Override
    public void splitPayment(final CommandInput paymentDetails) {
        transactionService.splitPayment(paymentDetails);
    }

    @Override
    public void splitPaymentResponse(final ArrayNode output, final CommandInput response,
                                     final ExchangeInput[] exchangeRates) {

        transactionService.splitPaymentResponse(output, response, exchangeRates);
    }

    @Override
    public Report generateReport(final CommandInput reportDetails) {
        return reportingService.generateReport(reportDetails);
    }

    @Override
    public void withdrawSavings(final CommandInput withdrawalDetails,
                                final ExchangeInput[] exchangeRates) {

        transactionService.withdrawSavings(withdrawalDetails, exchangeRates);
    }

    @Override
    public void upgradePlan(final CommandInput accountDetails,
                            final ExchangeInput[] exchangeRates,
                            final ArrayNode output) {

        servicePlanManager.upgradePlan(accountDetails, accountDetails.getNewPlanType(),
                exchangeRates, output);
    }

    @Override
    public String cashWithdrawal(final CommandInput withdrawalDetails,
                               final ExchangeInput[] exchangeRates) {

        return transactionService.cashWithdrawal(withdrawalDetails, exchangeRates);
    }
}
