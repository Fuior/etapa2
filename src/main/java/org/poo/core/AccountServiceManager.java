package org.poo.core;

import lombok.Getter;
import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.ClassicAccount;
import org.poo.models.SavingAccount;
import org.poo.models.Transaction;
import org.poo.models.UserDetails;

@Getter
public final class AccountServiceManager extends BankRepositoryEntity implements ResourceManager {

    private String error;

    public AccountServiceManager(final BankRepository bankRepository) {

        super(bankRepository);
        error = null;
    }

    @Override
    public void add(final CommandInput accountDetails) {

        UserDetails user = bankRepository.findUser(accountDetails.getEmail());
        AccountService bankAccount;

        if (accountDetails.getAccountType().equals("savings")) {
            bankAccount = new SavingAccount(accountDetails.getCurrency(),
                    accountDetails.getAccountType(), accountDetails.getTimestamp());

            ((SavingAccount) bankAccount).setInterestRate(accountDetails.getInterestRate());
        } else {
            bankAccount = new ClassicAccount(accountDetails.getCurrency(),
                    accountDetails.getAccountType(), accountDetails.getTimestamp());
        }

        user.getBankAccounts().add(bankAccount);
        user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                "New account created"));

        bankRepository.addAccount(bankAccount);
        bankRepository.addUserByAccount(user, bankAccount.getIban());
    }

    @Override
    public void delete(final CommandInput accountDetails) {

        AccountService bankAccount = bankRepository.findAccountByIBAN(accountDetails.getAccount());

        String message = "Account couldn't be deleted - see org.poo.transactions for details";

        if (bankAccount == null) {
            error = message;
            return;
        }

        UserDetails user = bankRepository.findUserByAccount(bankAccount);

        if (bankAccount.getBalance() != 0) {

            user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                    "Account couldn't be deleted - there are funds remaining"));

            error = message;
            return;
        }

        bankRepository.deleteUser(bankAccount);
        bankRepository.deleteAccount(bankAccount.getIban());

        if ((user.getAlias() != null) && (user.getAlias().account() == bankAccount)) {
            bankRepository.deleteAccount(user.getAlias().name());
        }

        bankAccount.getCards().clear();
        user.getBankAccounts().removeIf(a -> a.getIban().equals(bankAccount.getIban()));
    }

    /**
     * Aceasta metoda adauga fonduri intr-un cont
     *
     * @param fundsDetails datele contului si valoare fondurilor
     */
    public void addFunds(final CommandInput fundsDetails) {

        AccountService account = bankRepository.findAccountByIBAN(fundsDetails.getAccount());

        if (account != null) {
            account.setBalance(account.getBalance() + fundsDetails.getAmount());
        }
    }

    /**
     * Aceasta metoda seteaza o balanta minima pentru un cont
     *
     * @param balanceInput datele actiunii
     */
    public void setMinBalance(final CommandInput balanceInput) {

        AccountService account = bankRepository.findAccountByIBAN(balanceInput.getAccount());

        if (account == null) {
            return;
        }

        account.setMinBalance(balanceInput.getAmount());
    }

    /**
     * Aceasta metoda seteaza un alias pentru un cont
     *
     * @param aliasDetails datele actiunii
     */
    public void setAlias(final CommandInput aliasDetails) {

        UserDetails user = bankRepository.findUser(aliasDetails.getEmail());
        AccountService account = bankRepository.findAccountByIBAN(aliasDetails.getAccount());

        user.setAlias(new UserDetails.Alias(aliasDetails.getAlias(), account));
        bankRepository.addAccountByAlias(account, aliasDetails.getAlias());
    }

    /**
     * Aceasta metoda genereaza incasarea dobanzii
     *
     * @param interestDetails datele contului care incaseaza dobanda
     * @return valoarea de succes a acestei actiuni
     */
    public int addInterest(final CommandInput interestDetails) {

        AccountService accout = bankRepository.findAccountByIBAN(interestDetails.getAccount());

        if (accout == null) {
            return 0;
        }

        if (accout.getAccountType().equals("classic")) {
            return -1;
        }

        double interest = accout.getBalance() * ((SavingAccount) accout).getInterestRate();
        accout.setBalance(accout.getBalance() + interest);

        return 0;
    }

    /**
     * Aceasta metoda schimba dobanda unui cont
     *
     * @param interestDetails detaliile contului si valoarea dobanzii
     * @return valoarea de succes a acestei actiuni
     */
    public int changeInterestRate(final CommandInput interestDetails) {

        AccountService accout = bankRepository.findAccountByIBAN(interestDetails.getAccount());

        if (accout == null) {
            return 0;
        }

        if (accout.getAccountType().equals("classic")) {
            return -1;
        }

        ((SavingAccount) accout).setInterestRate(interestDetails.getInterestRate());
        String description = "Interest rate of the account changed to "
                            + ((SavingAccount) accout).getInterestRate();

        UserDetails user = bankRepository.findUserByAccount(accout);
        user.getTransactions().add(new Transaction(interestDetails.getTimestamp(), description));

        return 0;
    }
}
