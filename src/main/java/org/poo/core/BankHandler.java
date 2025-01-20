package org.poo.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Data;
import org.poo.core.service.ServiceHandler;
import org.poo.core.service.report.Report;
import org.poo.core.transactions.TransactionService;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.user.UserDetails;

import java.util.ArrayList;

@Data
public final class BankHandler implements IBankHandler {

    private ArrayList<UserDetails> users;
    private BankRepository bankRepository;
    private ServiceHandler serviceHandler;
    private TransactionService transactionService;

    public BankHandler(final ArrayList<UserDetails> users, final BankRepository bankRepository,
                       final ExchangeInput[] exchangeRates, final CommerciantInput[] commerciants) {

        this.users = users;
        this.bankRepository = bankRepository;
        this.serviceHandler = new ServiceHandler(bankRepository, exchangeRates);
        this.transactionService = new TransactionService(bankRepository,
                serviceHandler, exchangeRates, commerciants);
    }

    @Override
    public void addAccount(final CommandInput accountDetails,
                           final CommerciantInput[] commerciants) {

        serviceHandler.addAccount(accountDetails, commerciants);
    }

    @Override
    public String deleteAccount(final CommandInput accountDetails) {
        return serviceHandler.deleteAccount(accountDetails);
    }

    @Override
    public void addNewBusinessAssociate(final CommandInput associateDetails) {
        serviceHandler.addNewBusinessAssociate(associateDetails);
    }

    @Override
    public void addFunds(final CommandInput fundsDetails) {
        serviceHandler.addFunds(fundsDetails);
    }

    @Override
    public void setMinBalance(final CommandInput balanceInput) {
        serviceHandler.setMinBalance(balanceInput);
    }

    @Override
    public void setAlias(final CommandInput aliasDetails) {
        serviceHandler.setAlias(aliasDetails);
    }

    @Override
    public int addInterest(final CommandInput interestDetails) {
        return serviceHandler.addInterest(interestDetails);
    }

    @Override
    public int changeInterestRate(final CommandInput interestDetails) {
        return serviceHandler.changeInterestRate(interestDetails);
    }

    @Override
    public String changeMoneyLimit(final CommandInput limitDetails) {
        return serviceHandler.changeMoneyLimit(limitDetails);
    }

    @Override
    public void addCard(final CommandInput cardDetails, final CommerciantInput[] commerciants) {
        serviceHandler.addCard(cardDetails, commerciants);
    }

    @Override
    public void deleteCard(final CommandInput cardDetails) {
        serviceHandler.deleteCard(cardDetails);
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
        return serviceHandler.generateReport(reportDetails);
    }

    @Override
    public void withdrawSavings(final CommandInput withdrawalDetails) {
        transactionService.executeTransaction(withdrawalDetails);
    }

    @Override
    public void upgradePlan(final CommandInput accountDetails,
                            final ExchangeInput[] exchangeRates,
                            final ArrayNode output) {

        serviceHandler.upgradePlan(accountDetails, accountDetails.getNewPlanType(),
                exchangeRates, output);
    }

    @Override
    public String cashWithdrawal(final CommandInput withdrawalDetails) {
        return transactionService.executeTransaction(withdrawalDetails);
    }
}
