package org.poo.core.service.card;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

public final class AddCardCommand implements Command {

    private final CardServiceManager cardServiceManager;
    private final CommandInput cardDetails;
    private final CommerciantInput[] commerciants;

    public AddCardCommand(final CardServiceManager cardServiceManager,
                          final CommandInput cardDetails,
                          final CommerciantInput[] commerciants) {

        this.cardServiceManager = cardServiceManager;
        this.cardDetails = cardDetails;
        this.commerciants = commerciants;
    }

    @Override
    public void execute() {
        cardServiceManager.add(cardDetails, commerciants);
    }
}
