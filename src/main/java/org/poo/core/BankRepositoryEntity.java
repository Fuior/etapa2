package org.poo.core;

public abstract class BankRepositoryEntity {

    protected final BankRepository bankRepository;

    public BankRepositoryEntity(final BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }
}
