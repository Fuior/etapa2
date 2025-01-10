package org.poo.core;

import org.poo.fileio.CommandInput;

public interface ResourceManager {

    /**
     * Aceasta metoda creeaza o noua resursa pentru un user
     *
     * @param commandInput detaliile resursei ce va fi adaugata
     */
    void add(CommandInput commandInput);

    /**
     * Aceasta metoda sterge o resursa a unui user
     *
     * @param commandInput detaliile resursei ce va fi stearsa
     */
    void delete(CommandInput commandInput);
}
