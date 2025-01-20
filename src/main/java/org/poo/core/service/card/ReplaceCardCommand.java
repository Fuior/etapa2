package org.poo.core.service.card;

import org.poo.core.service.Command;
import org.poo.models.account.AccountService;
import org.poo.models.card.CardDetails;
import org.poo.models.user.UserDetails;

public final class ReplaceCardCommand implements Command {

    private final CardServiceManager cardServiceManager;
    private final UserDetails user;
    private final AccountService account;
    private final CardDetails card;
    private final int timestamp;

    public ReplaceCardCommand(final CardServiceManager cardServiceManager, final UserDetails user,
                              final AccountService account, final CardDetails card,
                              final int timestamp) {

        this.cardServiceManager = cardServiceManager;
        this.user = user;
        this.account = account;
        this.card = card;
        this.timestamp = timestamp;
    }

    @Override
    public void execute() {
        cardServiceManager.replaceCard(user, account, card, timestamp);
    }
}
