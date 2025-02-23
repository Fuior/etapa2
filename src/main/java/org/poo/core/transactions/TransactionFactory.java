package org.poo.core.transactions;

import org.poo.core.BankRepository;
import org.poo.core.service.ServiceHandler;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

import java.util.List;

public final class TransactionFactory {

    private TransactionFactory() { }

    /**
     * Aceasta metoda instantiaza clasa aferenta operatiei
     * ce urmeaza a fi executate.
     *
     * @param operationType tipul operatiei
     * @param bankRepository o instanta a clasei BankRepository
     * @param serviceHandler o instanta a clasei ServiceHandler
     * @param commerciants lista de comercianti
     * @param splitPayments datele comenzilor pentru platile distribuite
     * @param splitPaymentsResponse raspunsurile user-ilor implicati intr-o plata distribuita
     * @return instanta clasei aferente operatiei
     */
    public static TransactionOperation getOperation(
            final String operationType, final BankRepository bankRepository,
            final ServiceHandler serviceHandler, final CommerciantInput[] commerciants,
            final List<CommandInput> splitPayments,
            final List<CommandInput> splitPaymentsResponse) {

        return switch (operationType) {
            case "payOnline" ->
                    new PayOnlineOperation(bankRepository, serviceHandler, commerciants);

            case "sendMoney" -> new SendMoneyOperation(bankRepository, commerciants);

            case "splitPayment", "acceptSplitPayment", "rejectSplitPayment" ->
                    new SplitPaymentOperation(bankRepository, splitPayments, splitPaymentsResponse);

            case "withdrawSavings" -> new WithdrawSavingsOperation(bankRepository);

            case "cashWithdrawal" -> new CashWithdrawalOperation(bankRepository);

            default -> null;
        };
    }
}
