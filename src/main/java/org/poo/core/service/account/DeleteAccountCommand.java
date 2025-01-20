package org.poo.core.service.account;

import lombok.Getter;
import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

@Getter
public final class DeleteAccountCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput accountDetails;
    private String error;

    public DeleteAccountCommand(final AccountServiceManager accountServiceManager,
                                final CommandInput accountDetails) {

        this.accountServiceManager = accountServiceManager;
        this.accountDetails = accountDetails;
    }

    @Override
    public void execute() {
        accountServiceManager.delete(accountDetails);
        error = accountServiceManager.getError();
    }
}
