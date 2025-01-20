package org.poo.models.errors;

import lombok.Getter;

@Getter
public class SuccessfulDelete {

    private final String success = "Account deleted";
    private final int timestamp;

    public SuccessfulDelete(final int timestamp) {
        this.timestamp = timestamp;
    }
}
