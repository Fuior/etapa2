package org.poo.core.service.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.poo.models.account.AccountService;

@Getter
public class ReportFormat {

    @JsonProperty("IBAN")
    private final String iban;
    private final double balance;
    private final String currency;

    public ReportFormat(final AccountService account) {

        this.iban = account.getIban();
        this.balance = account.getBalance();
        this.currency = account.getCurrency();
    }
}
