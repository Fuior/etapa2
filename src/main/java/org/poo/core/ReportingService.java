package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.models.AccountService;
import org.poo.models.UserDetails;

public class ReportingService extends BankRepositoryEntity {

    public ReportingService(final BankRepository bankRepository) {
        super(bankRepository);
    }

    /**
     * Aceasta metoda genereaza un report de cheltuieli
     *
     * @param reportDetails detaliile contului pentru care se genereaza reportul
     * @return reportul creat
     */
    public Report generateReport(final CommandInput reportDetails) {

        AccountService account = bankRepository.findAccountByIBAN(reportDetails.getAccount());

        if (account == null) {
            return null;
        }

        UserDetails user = bankRepository.findUserByAccount(account);

        Report report = new Report(reportDetails.getCommand(), reportDetails.getTimestamp());
        report.setOutput(reportDetails, user, account);

        return report;
    }
}
