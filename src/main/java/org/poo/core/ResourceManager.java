package org.poo.core;

import org.poo.fileio.CommandInput;
import org.poo.fileio.CommerciantInput;

public interface ResourceManager {

    /**
     * Aceasta metoda creeaza o noua resursa pentru un user
     *
     * @param commandInput detaliile resursei ce va fi adaugata
     */
    void add(CommandInput commandInput, CommerciantInput[] commerciants);

    /**
     * Aceasta metoda sterge o resursa a unui user
     *
     * @param commandInput detaliile resursei ce va fi stearsa
     */
    void delete(CommandInput commandInput);
}
