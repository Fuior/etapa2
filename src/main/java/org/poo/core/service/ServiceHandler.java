package org.poo.core.service;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.core.BankRepository;
import org.poo.core.service.account.AccountServiceManager;
import org.poo.core.service.account.AddAccountCommand;
import org.poo.core.service.account.AddFundsCommand;
import org.poo.core.service.account.AddInterestCommand;
import org.poo.core.service.account.AddNewBusinessAssociateCommand;
import org.poo.core.service.account.ChangeInterestRateCommand;
import org.poo.core.service.account.ChangeMoneyLimitCommand;
import org.poo.core.service.account.DeleteAccountCommand;
import org.poo.core.service.account.SetAliasCommand;
import org.poo.core.service.account.SetMinBalanceCommand;
import org.poo.core.service.card.AddCardCommand;
import org.poo.core.service.card.CardServiceManager;
import org.poo.core.service.card.DeleteCardCommand;
import org.poo.core.service.card.ReplaceCardCommand;
import org.poo.core.service.report.GenerateReportCommand;
import org.poo.core.service.report.Report;
import org.poo.core.service.report.ReportingService;
import org.poo.core.service.user.ServicePlanManager;
import org.poo.core.service.user.UpgradePlanCommand;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;
import org.poo.models.account.AccountService;
import org.poo.models.card.CardDetails;
import org.poo.models.user.UserDetails;

public final class ServiceHandler {

    private final CommandInvoker invoker;
    private final AccountServiceManager accountServiceManager;
    private final CardServiceManager cardServiceManager;
    private final ReportingService reportingService;
    private final ServicePlanManager servicePlanManager;

    public ServiceHandler(final BankRepository bankRepository,
                          final ExchangeInput[] exchangeRates) {

        this.invoker = new CommandInvoker();
        this.accountServiceManager = new AccountServiceManager(bankRepository, exchangeRates);
        this.cardServiceManager = new CardServiceManager(bankRepository);
        this.reportingService = new ReportingService(bankRepository);
        this.servicePlanManager = new ServicePlanManager(bankRepository);
    }

    /**
     * Aceasta metoda adauga un cont
     *
     * @param accountDetails datele actiunii
     * @param commerciants comerciantii
     */
    public void addAccount(final CommandInput accountDetails,
                           final CommerciantInput[] commerciants) {

        AddAccountCommand command =
                new AddAccountCommand(accountServiceManager, accountDetails, commerciants);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda sterge un cont
     *
     * @param accountDetails datele contului
     * @return null daca s-a sters contul sau un mesaj de eroare in caz contrar
     */
    public String deleteAccount(final CommandInput accountDetails) {

        DeleteAccountCommand command =
                new DeleteAccountCommand(accountServiceManager, accountDetails);

        invoker.executeCommand(command);
        return command.getError();
    }

    /**
     * Aceasta metoda adauga un asociat la un cont de business
     *
     * @param associateDetails datele asociatului
     */
    public void addNewBusinessAssociate(final CommandInput associateDetails) {

        AddNewBusinessAssociateCommand command =
                new AddNewBusinessAssociateCommand(accountServiceManager, associateDetails);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda adauga fonduri intr-un cont
     *
     * @param fundsDetails datele contului si valoare fondurilor
     */
    public void addFunds(final CommandInput fundsDetails) {

        AddFundsCommand command = new AddFundsCommand(accountServiceManager, fundsDetails);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda seteaza o balanta minima pentru un cont
     *
     * @param balanceInput datele actiunii
     */
    public void setMinBalance(final CommandInput balanceInput) {

        SetMinBalanceCommand command =
                new SetMinBalanceCommand(accountServiceManager, balanceInput);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda seteaza un alias pentru un cont
     *
     * @param aliasDetails datele actiunii
     */
    public void setAlias(final CommandInput aliasDetails) {

        SetAliasCommand command = new SetAliasCommand(accountServiceManager, aliasDetails);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda genereaza incasarea dobanzii
     *
     * @param interestDetails datele contului care incaseaza dobanda
     * @return valoarea de succes a acestei actiuni
     */
    public int addInterest(final CommandInput interestDetails) {

        AddInterestCommand command = new AddInterestCommand(accountServiceManager, interestDetails);

        invoker.executeCommand(command);
        return command.getResult();
    }

    /**
     * Aceasta metoda schimba dobanda unui cont
     *
     * @param interestDetails detaliile contului si valoarea dobanzii
     * @return valoarea de succes a acestei actiuni
     */
    public int changeInterestRate(final CommandInput interestDetails) {

        ChangeInterestRateCommand command =
                new ChangeInterestRateCommand(accountServiceManager, interestDetails);

        invoker.executeCommand(command);
        return command.getResult();
    }

    /**
     * Aceasta metoda schimba limita de cheltuieli sau de depus bani
     * pentru un cont de business.
     *
     * @param limitDetails datele necesare operatiei
     * @return mesajul de eroare al operatiei
     */
    public String changeMoneyLimit(final CommandInput limitDetails) {

        ChangeMoneyLimitCommand command =
                new ChangeMoneyLimitCommand(accountServiceManager, limitDetails);

        invoker.executeCommand(command);
        return command.getMessage();
    }

    /**
     * Aceasta metoda creeaza un card nou
     *
     * @param cardDetails detaliile necesare pentru crearea cardului
     */
    public void addCard(final CommandInput cardDetails, final CommerciantInput[] commerciants) {

        AddCardCommand command =
                new AddCardCommand(cardServiceManager, cardDetails, commerciants);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda sterge un card
     *
     * @param cardDetails detaliile cardului
     */
    public void deleteCard(final CommandInput cardDetails) {

        DeleteCardCommand command = new DeleteCardCommand(cardServiceManager, cardDetails);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda sterge un card "one time card"
     * si genereaza unul nou in locul acestuia
     *
     * @param user user-ul care detine cardul
     * @param account contul cu care este asociat cardul
     * @param card datele cardului
     * @param timestamp momentul de timp la care are loc actiunea
     */
    public void replaceCard(final UserDetails user, final AccountService account,
                            final CardDetails card, final int timestamp) {

        ReplaceCardCommand command =
                new ReplaceCardCommand(cardServiceManager, user, account, card, timestamp);

        invoker.executeCommand(command);
    }

    /**
     * Aceasta metoda genereaza un report de cheltuieli
     *
     * @param reportDetails detaliile contului pentru care se genereaza reportul
     * @return reportul creat
     */
    public Report generateReport(final CommandInput reportDetails) {

        GenerateReportCommand command =
                new GenerateReportCommand(reportingService, reportDetails);

        invoker.executeCommand(command);
        return command.getReport();
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

        UpgradePlanCommand command = new UpgradePlanCommand(servicePlanManager,
                accountDetails, newPlanType, exchangeRates, output);

        invoker.executeCommand(command);
    }
}
