package com.bank.model;

import com.bank.patterns.Visitable;
import com.bank.patterns.Visitor;
import java.util.UUID;

public class Account implements Visitable {
    private final UUID id;
    private final UUID userUuid;
    private double balance;
    private boolean isFrozen;

    public Account(UUID userUuid) {
        this(UUID.randomUUID(), userUuid, 0.0, false);
    }

    public Account(UUID id, UUID userUuid, double balance, boolean isFrozen) {
        this.id = id;
        this.userUuid = userUuid;
        this.balance = balance;
        this.isFrozen = isFrozen;
    }

    public UUID getId() { return id; }
    public UUID getUserUuid() { return userUuid; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public boolean isFrozen() { return isFrozen; }
    public void setFrozen(boolean frozen) { isFrozen = frozen; }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}