package org.poo.fileio;

import lombok.Getter;

@Getter
public class ReportErrorOutput {

    private String command;
    private ErrorOutput output;
    private int timestamp;

    public ReportErrorOutput(final String command, final int timestamp) {

        this.command = command;
        this.output = new ErrorOutput("Account not found", timestamp);
        this.timestamp = timestamp;
    }
}
