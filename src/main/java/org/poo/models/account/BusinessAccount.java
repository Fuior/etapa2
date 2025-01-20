package org.poo.models.account;

import lombok.Getter;
import lombok.Setter;
import org.poo.fileio.CommandInput;
import org.poo.models.user.UserDetails;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BusinessAccount extends AccountService {

    private UserDetails owner;
    private List<BusinessAssociate> managers;
    private List<BusinessAssociate> employees;
    private double spendingLimit;
    private double depositLimit;
    private List<CommerciantFormat> commerciants;

    public BusinessAccount(final CommandInput accountDetails) {

        super(accountDetails);
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.commerciants = new ArrayList<>();
    }

    /**
     * Aceasta metoda verifica daca un asociat are rol de angajat.
     *
     * @param user datele asociatului
     * @return valoarea de adevar
     */
    public boolean isEmployee(final UserDetails user) {

        for (BusinessAssociate associate : employees) {
            if (associate.getUserInput() == user.getUserInput()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Aceasta metoda cauta un asociat dupa email-ul sau.
     *
     * @param email email-ul asociatului
     * @return asociatul cautat sau null daca nu exista
     */
    public BusinessAssociate findAssociate(final String email) {

        if (owner.getUserInput().getEmail().equals(email)) {
            return null;
        }

        for (BusinessAssociate associate : managers) {
            if (associate.getUserInput().getEmail().equals(email)) {
                return associate;
            }
        }

        for (BusinessAssociate associate : employees) {
            if (associate.getUserInput().getEmail().equals(email)) {
                return associate;
            }
        }

        return null;
    }

    /**
     * Aceasta metoda adauga o plata facuta la un comerciant comerciant
     * in lista de plati pentru comercinati.
     *
     * @param associate asociatul care a facut plata
     * @param name numele comerciantului
     * @param amount suma platita
     */
    public void addCommerciant(final BusinessAssociate associate,
                               final String name, final double amount) {

        CommerciantFormat commerciant = findCommerciant(name);
        String associateName = associate.getUserInput().getLastName() + " "
                + associate.getUserInput().getFirstName();

        if (commerciant != null) {
            commerciant.setTotalReceived(commerciant.getTotalReceived() + amount);

            if (isEmployee(associate)) {
                commerciant.getEmployees().add(associateName);
            } else {
                commerciant.getManagers().add(associateName);
            }
        } else {
            commerciant = new CommerciantFormat(name);
            commerciant.setTotalReceived(amount);

            if (isEmployee(associate)) {
                commerciant.getEmployees().add(associateName);
            } else {
                commerciant.getManagers().add(associateName);
            }

            commerciants.add(commerciant);
        }
    }

    private CommerciantFormat findCommerciant(final String name) {

        for (CommerciantFormat commerciant : commerciants) {
            if (commerciant.getCommerciant().equals(name)) {
                return commerciant;
            }
        }

        return null;
    }
}
