package org.poo.core.service.account;

import lombok.Getter;
import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

@Getter
public final class AddInterestCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput interestDetails;
    private int result;

    public AddInterestCommand(final AccountServiceManager accountServiceManager,
                              final CommandInput interestDetails) {

        this.accountServiceManager = accountServiceManager;
        this.interestDetails = interestDetails;
    }

    @Override
    public void execute() {
        result = accountServiceManager.addInterest(interestDetails);
    }
}
