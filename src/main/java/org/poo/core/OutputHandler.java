package org.poo.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.core.service.report.Report;
import org.poo.fileio.CommandInput;
import org.poo.fileio.DeleteAccountOutput;
import org.poo.fileio.ErrorOutput;
import org.poo.fileio.OutputGenerator;
import org.poo.fileio.SimpleOutput;
import org.poo.fileio.SpendingsErrorOutput;
import org.poo.fileio.TransactionsOutput;
import org.poo.models.card.CardStatus;
import org.poo.models.errors.FailedDelete;
import org.poo.models.errors.SuccessfulDelete;

public class OutputHandler {

    private final CommandInput commandInput;
    private final BankHandler bank;
    private final ObjectMapper objectMapper;
    private final ArrayNode output;

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
     */
    public void payOnline() {

        String message = bank.payOnline(commandInput);

        if (message != null) {
            ErrorOutput myOutput = new ErrorOutput(commandInput, message);
            output.add(objectMapper.valueToTree(myOutput));
        }
    }

    /**
     * Aceasta metoda genereaza "outputul" unui transfer bancar.
     */
    public void sendMoney() {

        String message = bank.sendMoney(commandInput);

        if (message != null) {
            SimpleOutput myOutput = new SimpleOutput(commandInput, message);
            output.add(objectMapper.valueToTree(myOutput));
        }
    }

    /**
     * Aceasta metoda genereaza "outputul" unei plati distribuite.
     */
    public void splitPaymentOutput() {

        String message = bank.splitPaymentResponse(commandInput);

        if (message != null) {
            SimpleOutput myOutput = new SimpleOutput(commandInput, message);
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

        SimpleOutput myOutput = new SimpleOutput(commandInput, "This is not a savings account");
        output.add(objectMapper.valueToTree(myOutput));
    }

    /**
     * Aceasta metoda genereaza "outputul" unui operatii
     * de schimbare a limitei de cheltuieli/depuneri de bani.
     */
    public void changeMoneyLimit() {

        String message = bank.changeMoneyLimit(commandInput);

        if (message != null) {
            SimpleOutput myOutput = new SimpleOutput(commandInput, message);
            output.add(objectMapper.valueToTree(myOutput));
        }
    }

    /**
     * Aceasta metoda genereaza un raport pentru un cont
     * si "outputul" care se fa afisa in fisierul de iesire.
     */
    public void getReport() {

        Report report = bank.generateReport(commandInput);

        if (report == null) {

            ErrorOutput myOutput = new ErrorOutput(commandInput,
                    "Account not found");

            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        if (report.getOutput() != null) {
            output.add(objectMapper.valueToTree(report));
        }
    }

    /**
     * Aceasta metoda genereaza un raport de cheltuieli pentru un cont
     * si "outputul" care se va afisa in fisierul de iesire.
     */
    public void getSpendingReport() {

        Report report = bank.generateReport(commandInput);

        if (report == null) {

            ErrorOutput myOutput = new ErrorOutput(commandInput,
                    "Account not found");

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

    /**
     * Aceasta metoda genereaza un raport de cheltuieli pentru un cont de business
     * si "outputul" care se va afisa in fisierul de iesire.
     */
    public void getBusinessReport() {

        Report report = bank.generateReport(commandInput);

        if (report == null) {

            ErrorOutput myOutput = new ErrorOutput(commandInput,
                    "Account not found");

            output.add(objectMapper.valueToTree(myOutput));
            return;
        }

        if (report.getOutput() != null) {
            output.add(objectMapper.valueToTree(report));
        }
    }

    /**
     * Aceasta metoda genereaza mesajul de eroare pentru
     * o retragere de numerar esuata.
     */
    public void cashWithdrawal() {

        String message = bank.cashWithdrawal(commandInput);

        if (message == null) {
            return;
        }

        SimpleOutput myOutput = new SimpleOutput(commandInput, message);
        output.add(objectMapper.valueToTree(myOutput));
    }
}
