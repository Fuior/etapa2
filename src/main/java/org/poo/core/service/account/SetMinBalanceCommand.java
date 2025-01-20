package org.poo.core.service.account;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

public final class SetMinBalanceCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput balanceInput;

    public SetMinBalanceCommand(final AccountServiceManager accountServiceManager,
                                final CommandInput balanceInput) {

        this.accountServiceManager = accountServiceManager;
        this.balanceInput = balanceInput;
    }

    @Override
    public void execute() {
        accountServiceManager.setMinBalance(balanceInput);
    }
}
