package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.UserInput;

@Getter
@Setter
public class BusinessAssociate extends UserDetails {

    private double moneySpent;
    private double moneyDeposited;

    public BusinessAssociate(final UserInput userInput) {

        super(userInput);
        this.moneySpent = 0;
        this.moneyDeposited = 0;
    }
}
