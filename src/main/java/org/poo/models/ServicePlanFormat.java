package org.poo.models;

import lombok.Getter;

@Getter
public class ServicePlanFormat extends  Transaction {

    private final String accountIBAN;
    private final String newPlanType;

    public ServicePlanFormat(final int timestamp, final String description,
                             final String accountIBAN, final String newPlanType) {

        super(timestamp, description);
        this.accountIBAN = accountIBAN;
        this.newPlanType = newPlanType;
    }
}
