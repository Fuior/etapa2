package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.fileio.ExchangeInput;
import org.poo.fileio.PayOnlineOutput;

public interface IBankHandler {

    /**
     * Aceasta metoda adauga un cont
     *
     * @param accountDetails datele actiunii
     */
    void addAccount(CommandInput accountDetails);

    /**
     * Aceasta metoda sterge un cont
     *
     * @param accountDetails datele contului
     * @return null daca s-a sters contul sau un mesaj de eroare in caz contrar
     */
    String deleteAccount(CommandInput accountDetails);

    /**
     * Aceasta metoda adauga fonduri intr-un cont
     *
     * @param fundsDetails datele contului si valoare fondurilor
     */
    void addFunds(CommandInput fundsDetails);

    /**
     * Aceasta metoda seteaza o balanta minima pentru un cont
     *
     * @param balanceInput datele actiunii
     */
    void setMinBalance(CommandInput balanceInput);

    /**
     * Aceasta metoda seteaza un alias pentru un cont
     *
     * @param aliasDetails datele actiunii
     */
    void setAlias(CommandInput aliasDetails);

    /**
     * Aceasta metoda genereaza incasarea dobanzii
     *
     * @param interestDetails datele contului care incaseaza dobanda
     * @return valoarea de succes a acestei actiuni
     */
    int addInterest(CommandInput interestDetails);

    /**
     * Aceasta metoda schimba dobanda unui cont
     *
     * @param interestDetails detaliile contului si valoarea dobanzii
     * @return valoarea de succes a acestei actiuni
     */
    int changeInterestRate(CommandInput interestDetails);

    /**
     * Aceasta metoda creeaza un card nou
     *
     * @param cardDetails detaliile necesare pentru crearea cardului
     */
    void addCard(CommandInput cardDetails);

    /**
     * Aceasta metoda sterge un card
     *
     * @param cardDetails detaliile cardului
     */
    void deleteCard(CommandInput cardDetails);

    /**
     * Aceasta metoda genereaza o plata cu cardul
     *
     * @param cardDetails detaliile platii
     * @param exchangeRates cursurile de schimb valutar
     * @return
     */
    PayOnlineOutput payOnline(CommandInput cardDetails, ExchangeInput[] exchangeRates);

    /**
     * Aceasta metoda face un transfer bancar
     *
     * @param transferDetails detaliile pentru transferul bancar
     * @param exchangeRates cursurile de schimb valutar
     */
    void sendMoney(CommandInput transferDetails, ExchangeInput[] exchangeRates);

    /**
     * Aceasta metoda face o plata distribuita
     *
     * @param paymentDetails detaliile platii
     * @param exchangeRates cursurile de schimb valutar
     */
    void splitPayment(CommandInput paymentDetails, ExchangeInput[] exchangeRates);

    /**
     * Aceasta metoda genereaza un report de cheltuieli
     *
     * @param reportDetails detaliile contului pentru care se genereaza reportul
     * @return reportul creat
     */
    Report generateReport(CommandInput reportDetails);
}
