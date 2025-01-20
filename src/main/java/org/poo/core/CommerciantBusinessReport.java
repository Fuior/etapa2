package org.poo.core;

import lombok.Getter;
import org.poo.models.BusinessAccount;
import org.poo.models.CommerciantFormat;

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
