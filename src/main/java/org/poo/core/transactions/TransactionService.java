package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.core.BankRepositoryEntity;
import org.poo.core.CardServiceManager;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;

import java.util.ArrayList;
import java.util.List;

public final class TransactionService extends BankRepositoryEntity {

    private final CardServiceManager cardServiceManager;
    private final ExchangeInput[] exchangeRates;
    private final CommerciantInput[] commerciants;
    private final List<CommandInput> splitPayments;
    private final List<CommandInput> splitPaymentsResponses;

    public TransactionService(final BankRepository bankRepository,
                              final CardServiceManager cardServiceManager,
                              final ExchangeInput[] exchangeRates,
                              final CommerciantInput[] commerciants) {

        super(bankRepository);
        this.cardServiceManager = cardServiceManager;
        this.exchangeRates = exchangeRates;
        this.commerciants = commerciants;
        this.splitPayments = new ArrayList<>();
        this.splitPaymentsResponses = new ArrayList<>();
    }

    /**
     * Aceasta metoda executa operatiile de tip Transaction
     *
     * @param commandInput datele operatiei
     * @return mesajul de eroare al executiei
     */
    public String executeTransaction(final CommandInput commandInput) {

        TransactionOperation operation = TransactionFactory.getOperation(commandInput.getCommand(),
                bankRepository, cardServiceManager, commerciants,
                splitPayments, splitPaymentsResponses);

        if (operation != null) {
            return operation.execute(commandInput, exchangeRates);
        }

        return null;
    }
}
