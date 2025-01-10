package org.poo.core;

import lombok.Data;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;
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

    public BankHandler(final ArrayList<UserDetails> users, final BankRepository bankRepository) {

        this.users = users;
        this.bankRepository = bankRepository;
        this.accountServiceManager = new AccountServiceManager(bankRepository);
        this.cardServiceManager = new CardServiceManager(bankRepository);
        this.transactionService = new TransactionService(bankRepository, cardServiceManager);
        this.reportingService = new ReportingService(bankRepository);
    }

    @Override
    public void addAccount(final CommandInput accountDetails) {
        accountServiceManager.add(accountDetails);
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
    public void addCard(final CommandInput cardDetails) {
        cardServiceManager.add(cardDetails);
    }

    @Override
    public void deleteCard(final CommandInput cardDetails) {
        cardServiceManager.delete(cardDetails);
    }

    @Override
    public PayOnlineOutput payOnline(final CommandInput cardDetails,
                                     final ExchangeInput[] exchangeRates) {

        return transactionService.payOnline(cardDetails, exchangeRates);
    }

    @Override
    public void sendMoney(final CommandInput transferDetails,
                          final ExchangeInput[] exchangeRates) {

        transactionService.sendMoney(transferDetails, exchangeRates);
    }

    @Override
    public void splitPayment(final CommandInput paymentDetails,
                             final ExchangeInput[] exchangeRates) {

        transactionService.splitPayment(paymentDetails, exchangeRates);
    }

    @Override
    public Report generateReport(final CommandInput reportDetails) {
        return reportingService.generateReport(reportDetails);
    }
}
