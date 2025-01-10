package org.poo.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.ObjectInput;
import org.poo.fileio.UserInput;
import org.poo.models.UserDetails;
import org.poo.utils.Utils;

import java.util.ArrayList;

public final class CommandProcessor {

    private ArrayList<UserDetails> getUsers(final UserInput[] usersInputs) {

        ArrayList<UserDetails> users = new ArrayList<>();

        for (UserInput user : usersInputs) {
            users.add(new UserDetails(user));
        }

        return users;
    }

    private BankRepository getBankRepository(final ArrayList<UserDetails> users) {

        BankRepository bank = BankRepository.getInstance();

        for (UserDetails user : users) {
            bank.addUserByEmail(user);
        }

        return bank;
    }

    /**
     * Aceasta metoda executa comenzile date la input,
     * apeland metoda specifice fiecarei actiuni.
     *
     * @param inputData datele de intrare pentru comenzi
     * @param objectMapper o instanta a {@link com.fasterxml.jackson.databind.ObjectMapper}
     * @param output ArrayNode in care se pun datele ce vor fi afisate in fisierul de iesire
     */
    public void execute(final ObjectInput inputData, final ObjectMapper objectMapper,
                        final ArrayNode output) {

        ArrayList<UserDetails> users = getUsers(inputData.getUsers());
        BankRepository bankRepository = getBankRepository(users);

        BankHandler bank = new BankHandler(users, bankRepository);
        Utils.resetRandom();

        for (CommandInput commandInput : inputData.getCommands()) {

            OutputHandler outputHandler;
            outputHandler = new OutputHandler(commandInput, bank, objectMapper, output);

            switch (commandInput.getCommand()) {
                case "printUsers" -> outputHandler.printUsers();

                case "addAccount" -> bank.addAccount(commandInput);

                case "createCard", "createOneTimeCard" -> bank.addCard(commandInput);

                case "addFunds" -> bank.addFunds(commandInput);

                case "deleteAccount" -> outputHandler.deleteAccount();

                case "deleteCard" -> bank.deleteCard(commandInput);

                case "payOnline" -> outputHandler.payOnline(inputData.getExchangeRates());

                case "sendMoney" -> bank.sendMoney(commandInput, inputData.getExchangeRates());

                case "setAlias" -> bank.setAlias(commandInput);

                case "printTransactions" -> outputHandler.printTransactions();

                case "checkCardStatus" -> outputHandler.checkCardStatus();

                case "setMinBalance" -> bank.setMinBalance(commandInput);

                case "splitPayment" -> bank.splitPayment(commandInput,
                                        inputData.getExchangeRates());

                case "addInterest", "changeInterestRate" -> outputHandler.interestRate();

                case "report" -> outputHandler.getReport(commandInput);

                case "spendingsReport" -> outputHandler.getSpendingReport(commandInput);

                default -> System.out.println("Invalid command.");
            }
        }
    }
}
