package org.poo.core;

import org.poo.models.AccountService;
import org.poo.models.CardDetails;
import org.poo.models.UserDetails;

import java.util.HashMap;
import java.util.Map;

public class BankRepository {

    private static BankRepository instance;
    private final Map<String, UserDetails> userMapByEmail = new HashMap<>();
    private final Map<String, UserDetails> userMapByAccount = new HashMap<>();
    private final Map<String, AccountService> accountMap = new HashMap<>();
    private final Map<String, AccountService> accountMapByCard = new HashMap<>();
    private final Map<String, CardDetails> cardMap = new HashMap<>();

    /**
     * Această metodă asigură că este creată o singură instanță a {@code BankRepository}
     *
     * @return instanța singleton a {@code BankRepository}
     */
    public static synchronized BankRepository getInstance() {

        if (instance == null) {
            instance = new BankRepository();
        }

        return instance;
    }

    /**
     * Aceasta metoda adauga un user
     *
     * @param user user-ul care va fi adaugat
     */
    public void addUserByEmail(final UserDetails user) {
        userMapByEmail.put(user.getUserInput().getEmail(), user);
    }

    /**
     * Aceasta metoda adauga un user
     *
     * @param user user-ul care va fi adaugat
     * @param account contul asociat user-ului
     */
    public void addUserByAccount(final UserDetails user, final String account) {
        userMapByAccount.put(account, user);
    }

    /**
     * Aceasta metoda sterge un user
     *
     * @param account contul asociat user-ului
     */
    public void deleteUser(final AccountService account) {
        userMapByAccount.remove(account.getIban());
    }

    /**
     * Aceasta metoda adauga un cont
     *
     * @param account contul care va fi adaugat
     */
    public void addAccount(final AccountService account) {
        accountMap.put(account.getIban(), account);
    }

    /**
     * Aceasta metoda adauga un cont
     *
     * @param account contul care va fi adaugat
     * @param alias alias-ul asociat contului
     */
    public void addAccountByAlias(final AccountService account, final String alias) {
        accountMap.put(alias, account);
    }

    /**
     * Aceasta metoda adauga un cont
     *
     * @param account contul care va fi adaugat
     * @param card cardul asociat contului
     */
    public void addAccountByCard(final AccountService account, final CardDetails card) {
        accountMapByCard.put(card.getCardNumber(), account);
    }

    /**
     * Aceasta metoda sterge un cont
     *
     * @param account iban-ul/alias-ul contului
     */
    public void deleteAccount(final String account) {
        accountMap.remove(account);
    }

    /**
     * Aceasta metoda sterge un cont
     *
     * @param card cardul asociat contului
     */
    public void deleteAccountByCard(final CardDetails card) {
        accountMapByCard.remove(card.getCardNumber());
    }

    /**
     * Aceasta metoda adauga un card
     *
     * @param card numarul cardului care va fi adaugat
     */
    public void addCard(final CardDetails card) {
        cardMap.put(card.getCardNumber(), card);
    }

    /**
     * Aceasta metoda sterge un card
     *
     * @param cardNumber numarului cardului care trebuie sters
     */
    public void deleteCard(final String cardNumber) {
        cardMap.remove(cardNumber);
    }

    /**
     * Aceasta metoda cauta un user dupa un email
     *
     * @param email email-ul user-ului
     * @return user-ul dorit sau null daca nu exista
     */
    public UserDetails findUser(final String email) {
        return userMapByEmail.get(email);
    }

    /**
     * Aceasta metoda cauta un user dupa un cont
     *
     * @param account iban-ul contului asociat user-ului
     * @return user-ul dorit sau null daca nu exista
     */
    public UserDetails findUserByAccount(final AccountService account) {
        return userMapByAccount.get(account.getIban());
    }

    /**
     * Aceasta metoda cauta un cont dupa un iban
     *
     * @param iban iban-ul contului
     * @return contul dorit sau null daca nu exista
     */
    public AccountService findAccountByIBAN(final String iban) {
        return accountMap.get(iban);
    }

    /**
     * Aceasta metoda cauta un cont dupa un card
     *
     * @param cardNumber numarul cardului asociat contului
     * @return contul dorit sau null daca nu exista
     */
    public AccountService findAccountByCard(final String cardNumber) {
        return accountMapByCard.get(cardNumber);
    }

    /**
     * Aceasta metoda cauta un card dupa numarului lui.
     *
     * @param cardNumber este numarul cardului
     * @return cardul dorit sau null daca nu exista
     */
    public CardDetails findCardByNumber(final String cardNumber) {
        return cardMap.get(cardNumber);
    }
}
