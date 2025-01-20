package org.poo.core.service.account;

import lombok.Getter;
import org.poo.core.BankRepository;
import org.poo.core.BankRepositoryEntity;
import org.poo.core.service.ResourceManager;
import org.poo.core.transactions.MoneyPayments;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.account.AccountService;
import org.poo.models.account.BusinessAccount;
import org.poo.models.account.BusinessAssociate;
import org.poo.models.account.ClassicAccount;
import org.poo.models.account.InterestFormat;
import org.poo.models.account.SavingsAccount;
import org.poo.models.transactions.Transaction;
import org.poo.models.user.UserDetails;

@Getter
public final class AccountServiceManager extends BankRepositoryEntity implements ResourceManager {

    private String error;
    private final ExchangeInput[] exchangeRates;
    private final MoneyPayments moneyPayments;

    public AccountServiceManager(final BankRepository bankRepository,
                                 final ExchangeInput[] exchangeRates) {

        super(bankRepository);
        this.error = null;
        this.exchangeRates = exchangeRates;
        this.moneyPayments = new MoneyPayments();
    }

    @Override
    public void add(final CommandInput accountDetails, final CommerciantInput[] commerciants) {

        UserDetails user = bankRepository.findUser(accountDetails.getEmail());
        AccountService bankAccount;

        if (accountDetails.getAccountType().equals("savings")) {
            bankAccount = new SavingsAccount(accountDetails);
            ((SavingsAccount) bankAccount).setInterestRate(accountDetails.getInterestRate());
        } else if (accountDetails.getAccountType().equals("classic")) {
            bankAccount = new ClassicAccount(accountDetails);
        } else {
            bankAccount = new BusinessAccount(accountDetails);
            ((BusinessAccount) bankAccount).setOwner(user);

            final double limitInRon = 500;
            double initialLimit = moneyPayments.getAmount(bankAccount.getCurrency(),
                    limitInRon, "RON", exchangeRates);

            ((BusinessAccount) bankAccount).setSpendingLimit(initialLimit);
            ((BusinessAccount) bankAccount).setDepositLimit(initialLimit);
        }

        bankAccount.setCommerciantTransactions(commerciants);

        user.getBankAccounts().add(bankAccount);

        if (!bankAccount.getAccountType().equals("business")) {
            user.getTransactions().add(new Transaction(accountDetails.getTimestamp(),
                    "New account created"));
        }

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

        if (bankAccount.getAccountType().equals("business")
                && ((BusinessAccount) bankAccount).getOwner() != user) {

            return;
        }

        if (bankAccount.getBalance() != 0.0) {

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
        bankAccount.getFoodTransactions().clear();
        bankAccount.getClothesTransactions().clear();
        bankAccount.getTechTransactions().clear();

        user.getBankAccounts().removeIf(a -> a.getIban().equals(bankAccount.getIban()));
        error = null;
    }

    /**
     * Aceasta metoda adauga un asociat la un cont de business
     *
     * @param associateDetails datele asociatului
     */
    public void addNewBusinessAssociate(final CommandInput associateDetails) {

        UserDetails user = bankRepository.findUser(associateDetails.getEmail());

        if (user == null) {
            return;
        }

        BusinessAccount account =
                (BusinessAccount) bankRepository.findAccountByIBAN(associateDetails.getAccount());

        if (account.findAssociate(user.getUserInput().getEmail()) != null
                || account.getOwner() == user) {

            return;
        }

        if (associateDetails.getRole().equals("manager")) {
            account.getManagers().add(new BusinessAssociate(user.getUserInput()));
        } else {
            account.getEmployees().add(new BusinessAssociate(user.getUserInput()));
        }
    }

    /**
     * Aceasta metoda adauga fonduri intr-un cont
     *
     * @param fundsDetails datele contului si valoare fondurilor
     */
    public void addFunds(final CommandInput fundsDetails) {

        AccountService account = bankRepository.findAccountByIBAN(fundsDetails.getAccount());

        if (account == null) {
            return;
        }

        if (account.getAccountType().equals("business")) {

            BusinessAccount businessAccount = (BusinessAccount) account;
            BusinessAssociate associate = businessAccount.findAssociate(fundsDetails.getEmail());

            if (associate != null) {

                if (businessAccount.isEmployee(associate)
                        && fundsDetails.getAmount() > businessAccount.getDepositLimit()) {

                    return;
                }

                associate.setMoneyDeposited(associate.getMoneyDeposited()
                        + fundsDetails.getAmount());
            } else if (!businessAccount.getOwner().getUserInput()
                    .getEmail().equals(fundsDetails.getEmail())) {

                return;
            }
        }

        account.setBalance(account.getBalance() + fundsDetails.getAmount());
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

        double interest = accout.getBalance() * ((SavingsAccount) accout).getInterestRate();
        accout.setBalance(accout.getBalance() + interest);

        UserDetails user = bankRepository.findUserByAccount(accout);
        user.getTransactions().add(new InterestFormat(interestDetails.getTimestamp(),
                "Interest rate income", interest, accout.getCurrency()));

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

        ((SavingsAccount) accout).setInterestRate(interestDetails.getInterestRate());
        String description = "Interest rate of the account changed to "
                            + ((SavingsAccount) accout).getInterestRate();

        UserDetails user = bankRepository.findUserByAccount(accout);
        user.getTransactions().add(new Transaction(interestDetails.getTimestamp(), description));

        return 0;
    }

    /**
     * Aceasta metoda schimba limita de cheltuieli sau de depus bani
     * pentru un cont de business.
     *
     * @param limitDetails datele necesare operatiei
     * @return mesajul de eroare al operatiei
     */
    public String changeMoneyLimit(final CommandInput limitDetails) {

        AccountService accountService = bankRepository.findAccountByIBAN(limitDetails.getAccount());

        if (!accountService.getAccountType().equals("business")) {
            return "This is not a business account";
        }

        BusinessAccount account = (BusinessAccount) accountService;

        if (!account.getOwner().getUserInput().getEmail().equals(limitDetails.getEmail())) {

            if (limitDetails.getCommand().equals("changeSpendingLimit")) {
                return "You must be owner in order to change spending limit.";
            } else {
                return "You must be owner in order to change deposit limit.";
            }
        }

        if (limitDetails.getCommand().equals("changeSpendingLimit")) {
            account.setSpendingLimit(limitDetails.getAmount());
        } else {
            account.setDepositLimit(limitDetails.getAmount());
        }

        return null;
    }
}
