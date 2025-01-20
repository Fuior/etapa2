package org.poo.models.account;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.UserInput;
import org.poo.models.user.UserDetails;

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
