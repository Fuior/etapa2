package org.poo.fileio;

import java.util.ArrayList;

public record UserOutput(String firstName, String lastName, String email,
                         ArrayList<AccountOutput> accounts) { }
