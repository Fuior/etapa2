package org.poo.fileio;

import lombok.Getter;
import org.poo.models.Transaction;

@Getter
public class SimpleOutput {

    private final String command;
    private final Transaction output;
    private final int timestamp;

    public SimpleOutput(final CommandInput commandInput, final String message) {

        this.command = commandInput.getCommand();
        this.output = new Transaction(commandInput.getTimestamp(), message);
        this.timestamp = commandInput.getTimestamp();
    }
}
