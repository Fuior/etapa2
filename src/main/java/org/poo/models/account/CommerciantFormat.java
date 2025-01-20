package org.poo.models.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommerciantFormat {

    private String commerciant;
    @JsonProperty("total received")
    private double totalReceived;
    private List<String> managers;
    private List<String> employees;

    public CommerciantFormat(final String name) {

        this.commerciant = name;
        this.totalReceived = 0;
        this.managers = new ArrayList<>();
        this.employees = new ArrayList<>();
    }
}
