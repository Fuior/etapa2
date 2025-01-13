package org.poo.models;

import lombok.Getter;

@Getter
public class SuccessfulDelete {

    private final String success = "Account deleted";
    private final int timestamp;

    public SuccessfulDelete(final int timestamp) {
        this.timestamp = timestamp;
    }
}
