package org.poo.fileio;

import lombok.Getter;

@Getter
public class ErrorOutput {

    private final String command;
    private final Output output;
    private final int timestamp;

    private record Output(String description, int timestamp) { }

    public ErrorOutput(final CommandInput commandInput, final String message) {

        this.command = commandInput.getCommand();
        this.output = new Output(message, commandInput.getTimestamp());
        this.timestamp = commandInput.getTimestamp();
    }
}
