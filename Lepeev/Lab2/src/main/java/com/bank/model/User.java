package com.bank.model;

import com.bank.patterns.Visitable;
import com.bank.patterns.Visitor;
import java.util.UUID;

public record User(UUID uuid, String nickname) implements Visitable {
    public User(String nickname) {
        this(UUID.randomUUID(), nickname);
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }
}