package org.poo.core.service.account;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

public final class SetAliasCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput aliasDetails;

    public SetAliasCommand(final AccountServiceManager accountServiceManager,
                           final CommandInput aliasDetails) {

        this.accountServiceManager = accountServiceManager;
        this.aliasDetails = aliasDetails;
    }

    @Override
    public void execute() {
        accountServiceManager.setAlias(aliasDetails);
    }
}
