package org.poo.fileio;

import lombok.Getter;

@Getter
public class SpendingsErrorOutput {

    private String command = "spendingsReport";
    private Error output = new Error("This kind of report is not supported for a saving account");
    private int timestamp;

    private record Error(String error) { }

    public SpendingsErrorOutput(final int timestamp) {
        this.timestamp = timestamp;
    }
}
