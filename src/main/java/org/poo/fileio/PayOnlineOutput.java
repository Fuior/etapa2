package org.poo.fileio;

import lombok.Getter;

@Getter
public class PayOnlineOutput {

    private final String command = "payOnline";
    private ErrorOutput output;
    private int timestamp;

    public PayOnlineOutput(final int timestamp) {

        this.output = new ErrorOutput("Card not found", timestamp);
        this.timestamp = timestamp;
    }
}
