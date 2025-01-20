package org.poo.core.service.account;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

public final class AddAccountCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput accountDetails;
    private final CommerciantInput[] commerciants;

    public AddAccountCommand(final AccountServiceManager accountServiceManager,
                             final CommandInput accountDetails,
                             final CommerciantInput[] commerciants) {

        this.accountServiceManager = accountServiceManager;
        this.accountDetails = accountDetails;
        this.commerciants = commerciants;
    }

    @Override
    public void execute() {
        accountServiceManager.add(accountDetails, commerciants);
    }
}
