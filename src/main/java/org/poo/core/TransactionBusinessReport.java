package org.poo.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.models.BusinessAccount;
import org.poo.models.BusinessAssociate;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionBusinessReport extends BusinessReportFormat {

    private List<Associate> managers;
    private List<Associate> employees;
    @JsonProperty("total spent")
    private double totalSpent;
    @JsonProperty("total deposited")
    private double totalDeposited;

    public TransactionBusinessReport(final BusinessAccount account,
                                     final String statisticsType) {

        super(account, statisticsType);
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
        this.totalSpent = 0;
        this.totalDeposited = 0;
    }

    private record Associate(String username, double spent, double deposited) { }

    /**
     * Aceasta metoda initializeaza campurile:
     * managers si employees
     *
     * @param managersList lista de menegeri
     * @param employeesList lista de angajati
     */
    public void addAssociates(final List<BusinessAssociate> managersList,
                             final List<BusinessAssociate> employeesList) {

        for (BusinessAssociate manager : managersList) {

            String username = manager.getUserInput().getLastName() + " "
                    + manager.getUserInput().getFirstName();

            managers.add(new Associate(username, manager.getMoneySpent(),
                    manager.getMoneyDeposited()));

            totalSpent += manager.getMoneySpent();
            totalDeposited += manager.getMoneyDeposited();
        }

        for (BusinessAssociate employee : employeesList) {

            String username = employee.getUserInput().getLastName() + " "
                    + employee.getUserInput().getFirstName();

            employees.add(new Associate(username, employee.getMoneySpent(),
                    employee.getMoneyDeposited()));

            totalSpent += employee.getMoneySpent();
            totalDeposited += employee.getMoneyDeposited();
        }
    }
}
