package org.poo.core.service;

public class CommandInvoker {

    /**
     * Aceasta metoda apeleaza metoda "execute()" din
     * clasele de tip "Command".
     *
     * @param command instanta unei clase de tip "Command"
     */
    public void executeCommand(final Command command) {
        command.execute();
    }
}
