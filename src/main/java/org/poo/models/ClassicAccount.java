package org.poo.models;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;

import java.util.ArrayList;

@Getter
@Setter
public class ClassicAccount extends  AccountService {

    private ArrayList<CardPaymentFormat> cardPayments;

    public ClassicAccount(final CommandInput accountDetails) {

        super(accountDetails);
        this.cardPayments = new ArrayList<>();
    }
}
