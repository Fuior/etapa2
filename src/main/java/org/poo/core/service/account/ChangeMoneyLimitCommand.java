package org.poo.core.service.account;

import lombok.Getter;
import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

@Getter
public final class ChangeMoneyLimitCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput limitDetails;
    private String message;

    public ChangeMoneyLimitCommand(final AccountServiceManager accountServiceManager,
                                   final CommandInput limitDetails) {

        this.accountServiceManager = accountServiceManager;
        this.limitDetails = limitDetails;
    }

    @Override
    public void execute() {
        message = accountServiceManager.changeMoneyLimit(limitDetails);
    }
}
