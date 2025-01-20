package org.poo.core.service.card;

import org.poo.core.service.Command;
import org.poo.fileio.CommandInput;

public final class DeleteCardCommand implements Command {

    private final CardServiceManager cardServiceManager;
    private final CommandInput cardDetails;

    public DeleteCardCommand(final CardServiceManager cardServiceManager,
                             final CommandInput cardDetails) {

        this.cardServiceManager = cardServiceManager;
        this.cardDetails = cardDetails;
    }

    @Override
    public void execute() {
        cardServiceManager.delete(cardDetails);
    }
}
