package org.poo.fileio;

import lombok.Getter;
import org.poo.models.Transaction;

@Getter
public class InterestRateOutput {

    private String command;
    private Transaction output;
    private int timestamp;

    public InterestRateOutput(final String command, final int timestamp) {

        this.command = command;
        this.output = new Transaction(timestamp, "This is not a savings account");
        this.timestamp = timestamp;
    }
}
