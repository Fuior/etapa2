package org.poo.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.poo.models.BusinessAccount;

@Getter
public class BusinessReportFormat extends ReportFormat {

    @JsonProperty("spending limit")
    private final double spendingLimit;
    @JsonProperty("deposit limit")
    private final double depositLimit;
    @JsonProperty("statistics type")
    private final String statisticsType;

    public BusinessReportFormat(final BusinessAccount account,
                                final String statisticsType) {

        super(account);
        this.spendingLimit = account.getSpendingLimit();
        this.depositLimit = account.getDepositLimit();
        this.statisticsType = statisticsType;
    }
}
