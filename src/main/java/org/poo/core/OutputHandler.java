package org.poo.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.DeleteAccountOutput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.InterestRateOutput;
import org.poo.fileio.OutputGenerator;
import org.poo.fileio.PayOnlineOutput;
import org.poo.fileio.ReportErrorOutput;
import org.poo.fileio.SpendingsErrorOutput;
import org.poo.fileio.TransactionsOutput;
import org.poo.models.FailedDelete;
import org.poo.models.SuccessfulDelete;

public class OutputHandler {

    private CommandInput commandInput;
    private BankHandler bank;
    private ObjectMapper objectMapper;
    private ArrayNode output;

    public OutputHandler(final CommandInput commandInput, final BankHandler bank,
                         final ObjectMapper objectMapper, final ArrayNode output) {

        this.commandInput = commandInput;
        this.bank = bank;
        this.objectMapper = objectMapper;
        this.output = output;
    }

    /**
     * Aceasta metoda afiseaza user-ii.
     */
    public void printUsers() {

        OutputGenerator myOutput;
        myOutput = new OutputGenerator("printUsers", commandInput.getTimestamp());

        myOutput.createUsersOutput(bank.getUsers());
        output.add(objectMapper.valueToTree(myOutput));
    }

    /**
     * Aceasta metoda sterge un cont si genereaza
     * "outputul" actiunii.
     */
    public void deleteAccount() {

        DeleteAccountOutput<Object> myOutput;
        myOutput = new DeleteAccountOutput<>(commandInput.getTimestamp());

        String error = bank.deleteAccount(commandInput);

        if (error == null) {
            myOutput.setOutput(new SuccessfulDelete(commandInput.getTimestamp()));
        } else {
            myOutput.setOutput(new FailedDelete(error, commandInput.getTimestamp()));
        }

        output.add(objectMapper.valueToTree(myOutput));
    }

    /**
     * Aceasta metoda genereaza "outputul" unei plati cu cardul.
     *
     * @param exchangeRates cursurile de schimb valutar
     */
    public void payOnline(final ExchangeInput[] exchangeRates) {

        PayOnlineOutput myOutput = bank.payOnline(commandInput, exchangeRates);

        if (myOutput != null) {
            output.add(objectMapper.valueToTree(myOutput));
        }
    }

    /**
     * Aceasta metoda verifica statusul unui card
     * si genereaza "outputul" aferent.
     */
    public void checkCardStatus() {

        CardStatus myOutput = new CardStatus(commandInput.getTimestamp());

        int result = myOutput.checkStatus(commandInput.getTimestamp(),
                commandInput.getCardNumber(), bank.getUsers());

        if (result == 0) {
            output.add(objectMapper.valueToTree(myOutput));
        }
    }

    /**
     * Aceasta metoda afiseaza tranzactiile unui cont
     */
    public void printTransactions() {

        TransactionsOutput myOutput = new TransactionsOutput(commandInput.getTimestamp());
        myOutput.setOutput(bank.getBankRepository(), commandInput.getEmail());
        output.add(objectMapper.valueToTree(myOutput));
    }

    /**
     * Aceasta metoda apeleaza metodele aferente si genereaza "outputul"
     * pentru comenzile "addInterest" si "changeInterestRate".
     */
    public void interestRate() {

        int result;

        if (commandInput.getCommand().equals("addInterest")) {
            result = bank.addInterest(commandInput);
        } else {
            result = bank.changeInterestRate(commandInput);
        }

        if (result == 0) {
            return;
        }

        InterestRateOutput myOutput = new InterestRateOutput(commandInput.getCommand(),
                                                            commandInput.getTimestamp());
        output.add(objectMapper.valueToTree(myOutput));
    }

    /**
     * Aceasta metoda genereaza un raport pentru un cont
     * si "outputul" care se fa afisa in fisierul de iesire.
     *
     * @param reportDetails detaliile actiunii
     */
    public void getReport(final CommandInput reportDetails) {

        Report report = bank.generateReport(commandInput);

        if (report == null) {
            ReportErrorOutput myOutput = new ReportErrorOutput(reportDetails.getCommand(),
                                                                reportDetails.getTimestamp());
            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        if (report.getOutput() != null) {
            output.add(objectMapper.valueToTree(report));
        }
    }

    /**
     * Aceasta metoda genereaza un raport de cheltuieli pentru un cont
     * si "outputul" care se fa afisa in fisierul de iesire.
     *
     * @param reportDetails detaliile actiunii
     */
    public void getSpendingReport(final CommandInput reportDetails) {

        Report report = bank.generateReport(commandInput);

        if (report == null) {

            ReportErrorOutput myOutput = new ReportErrorOutput(reportDetails.getCommand(),
                    reportDetails.getTimestamp());

            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        if (report.getOutput() != null) {
            output.add(objectMapper.valueToTree(report));
        } else {
            SpendingsErrorOutput myOutput = new SpendingsErrorOutput(commandInput.getTimestamp());
            output.add(objectMapper.valueToTree(myOutput));
        }
    }
}
