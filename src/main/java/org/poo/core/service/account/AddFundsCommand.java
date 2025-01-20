package org.poo.core.service.account;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

public final class AddFundsCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput fundsDetails;

    public AddFundsCommand(final AccountServiceManager accountServiceManager,
                           final CommandInput fundsDetails) {

        this.accountServiceManager = accountServiceManager;
        this.fundsDetails = fundsDetails;
    }

    @Override
    public void execute() {
        accountServiceManager.addFunds(fundsDetails);
    }
}
