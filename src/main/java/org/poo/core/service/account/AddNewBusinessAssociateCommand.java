package org.poo.core.service.account;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

public final class AddNewBusinessAssociateCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput associateDetails;

    public AddNewBusinessAssociateCommand(final AccountServiceManager accountServiceManager,
                                          final CommandInput associateDetails) {

        this.accountServiceManager = accountServiceManager;
        this.associateDetails = associateDetails;
    }

    @Override
    public void execute() {
        accountServiceManager.addNewBusinessAssociate(associateDetails);
    }
}
