package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.AccountService;
import org.poo.models.SavingsWithdrawalFormat;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

import java.time.LocalDate;
import java.time.Period;

public final class WithdrawSavingsOperation extends MoneyPayments implements TransactionOperation {

    private final BankRepository bankRepository;

    public WithdrawSavingsOperation(final BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public String execute(final CommandInput withdrawalDetails,
                          final ExchangeInput[] exchangeRates) {

        AccountService account = bankRepository.findAccountByIBAN(withdrawalDetails.getAccount());
        UserDetails user = bankRepository.findUserByAccount(account);

        AccountService receiver = user.findAccountByCurrency(withdrawalDetails.getCurrency());

        LocalDate birthDate = LocalDate.parse(user.getUserInput().getBirthDate());
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        double amount = getAmount(account.getCurrency(), withdrawalDetails.getAmount(),
                withdrawalDetails.getCurrency(), exchangeRates);

        String message;
        final int minAgeForTransaction = 21;

        if (age < minAgeForTransaction) {
            message = "You don't have the minimum age required.";
        } else if (!user.hasClassicAccount()) {
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

        return null;
    }
}
