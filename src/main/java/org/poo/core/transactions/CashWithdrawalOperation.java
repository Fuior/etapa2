package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.AccountService;
import org.poo.models.BusinessAccount;
import org.poo.models.CardDetails;
import org.poo.models.CashWithdrawalFormat;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

public final class CashWithdrawalOperation extends MoneyPayments implements TransactionOperation {

    private final BankRepository bankRepository;

    public CashWithdrawalOperation(final BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    @Override
    public String execute(final CommandInput withdrawalDetails,
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

        if (account.getAccountType().equals("business")) {

            if (((BusinessAccount) account).findAssociate(withdrawalDetails.getEmail()) == null
                    && ((BusinessAccount) account).getOwner() != user) {

                return "Card not found";
            }
        } else if (card.getCardHolder() != user) {
            return "Card not found";
        }

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

        double commission = calculateCommission(user, amount, withdrawalDetails.getAmount(), account);
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
