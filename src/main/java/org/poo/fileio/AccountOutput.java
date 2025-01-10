package org.poo.fileio;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


public record AccountOutput(@JsonProperty("IBAN") String iban, double balance, String currency,
                            String type, ArrayList<CardOutput> cards) { }
