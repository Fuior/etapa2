package org.poo.core.service.user;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;

public final class UpgradePlanCommand implements Command {

    private final ServicePlanManager servicePlanManager;
    private final CommandInput accountDetails;
    private final String newPlanType;
    private final ExchangeInput[] exchangeRates;
    private final ArrayNode output;

    public UpgradePlanCommand(final ServicePlanManager servicePlanManager,
                              final CommandInput accountDetails,
                              final String newPlanType,
                              final ExchangeInput[] exchangeRates,
                              final ArrayNode output) {

        this.servicePlanManager = servicePlanManager;
        this.accountDetails = accountDetails;
        this.newPlanType = newPlanType;
        this.exchangeRates = exchangeRates;
        this.output = output;
    }

    @Override
    public void execute() {
        servicePlanManager.upgradePlan(accountDetails, newPlanType, exchangeRates, output);
    }
}
