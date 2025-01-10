package org.poo.fileio;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeleteAccountOutput<T> {

    private final String command = "deleteAccount";
    private T output;
    private final int timestamp;

    public DeleteAccountOutput(final int timestamp) {
        this.timestamp = timestamp;
    }
}
