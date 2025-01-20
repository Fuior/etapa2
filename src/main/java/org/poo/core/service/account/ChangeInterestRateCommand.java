package org.poo.core.service.account;

import lombok.Getter;
import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

@Getter
public final class ChangeInterestRateCommand implements Command {

    private final AccountServiceManager accountServiceManager;
    private final CommandInput interestDetails;
    private int result;

    public ChangeInterestRateCommand(final AccountServiceManager accountServiceManager,
                                     final CommandInput interestDetails) {

        this.accountServiceManager = accountServiceManager;
        this.interestDetails = interestDetails;
    }

    @Override
    public void execute() {
        result = accountServiceManager.changeInterestRate(interestDetails);
    }
}
