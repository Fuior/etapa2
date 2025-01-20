package org.poo.core.transactions;

import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;

public interface TransactionOperation {

    /**
     * Aceasta metoda executa o operatie de tip Transaction
     *
     * @param commandInput datele operatiei
     * @param exchangeRates cursurile de schimb valutar
     * @return mesajul de eroare al operatiei
     */
    String execute(CommandInput commandInput, ExchangeInput[] exchangeRates);
}
