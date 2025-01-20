package org.poo.models.report;

import lombok.Getter;
import org.poo.models.account.CommerciantFormat;
import org.poo.models.account.BusinessAccount;

import java.util.List;

@Getter
public class CommerciantBusinessReport extends BusinessReportFormat {

    private final List<CommerciantFormat> commerciants;

    public CommerciantBusinessReport(final BusinessAccount account,
                                     final String statisticsType,
                                     final List<CommerciantFormat> commerciants) {

        super(account, statisticsType);
        this.commerciants = commerciants;
    }
}
