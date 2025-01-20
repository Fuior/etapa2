package org.poo.core;

import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;
import org.poo.fileio.ExchangeInput;

public interface IBankHandler {

    /**
     * Aceasta metoda adauga un cont
     *
     * @param accountDetails datele actiunii
     */
    void addAccount(CommandInput accountDetails, CommerciantInput[] commerciants);

    /**
     * Aceasta metoda sterge un cont
     *
     * @param accountDetails datele contului
     * @return null daca s-a sters contul sau un mesaj de eroare in caz contrar
     */
    String deleteAccount(CommandInput accountDetails);

    /**
     * Aceasta metoda adauga un asociat la un cont de business
     *
     * @param associateDetails datele asociatului
     */
    void addNewBusinessAssociate(CommandInput associateDetails);

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
     * Aceasta metoda schimba limita de cheltuieli sau de depus bani
     * pentru un cont de business.
     *
     * @param limitDetails datele necesare operatiei
     * @return mesajul de eroare al operatiei
     */
    String changeMoneyLimit(CommandInput limitDetails);

    /**
     * Aceasta metoda creeaza un card nou
     *
     * @param cardDetails detaliile necesare pentru crearea cardului
     */
    void addCard(CommandInput cardDetails, CommerciantInput[] commerciants);

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
     * @return mesajul de eroare sau null daca plata s-a efectuat cu succes
     */
    String payOnline(CommandInput cardDetails);

    /**
     * Aceasta metoda face un transfer bancar
     *
     * @param transferDetails detaliile pentru transferul bancar
     */
    String sendMoney(CommandInput transferDetails);

    /**
     * Aceasta metoda face o plata distribuita
     *
     * @param paymentDetails detaliile platii
     */
    void splitPayment(CommandInput paymentDetails);

    /**
     * Aceasta metoda accepta sau refuza o plata distribuita
     *
     * @param response raspunsul user-ului
     */
    String splitPaymentResponse(CommandInput response);

    /**
     * Aceasta metoda genereaza un report de cheltuieli
     *
     * @param reportDetails detaliile contului pentru care se genereaza reportul
     * @return reportul creat
     */
    Report generateReport(CommandInput reportDetails);

    /**
     * Aceasta metoda transfera bani dintr-un cont de economii
     * intr-un cont classic.
     *
     * @param withdrawalDetails datele necesare tranzactieie
     */
    void withdrawSavings(CommandInput withdrawalDetails);

    /**
     * Aceasta metoda face upgrade la planul unui user
     *
     * @param accountDetails datele contului pentru care se face upgrade la plan
     * @param exchangeRates cursurile de schimb valutar
     */
    void upgradePlan(CommandInput accountDetails, ExchangeInput[] exchangeRates, ArrayNode output);

    /**
     * Aceasta metoda retrage bani cash dintr-un cont
     *
     * @param withdrawalDetails datele necesare tranzactieie
     */
    String cashWithdrawal(CommandInput withdrawalDetails);
}
