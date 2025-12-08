package com.bank.model;

import java.util.UUID;

public record Transaction(UUID uuid, long timestamp, String type, double amount, UUID accountId) {
    public Transaction(String type, double amount, UUID accountId) {
        this(UUID.randomUUID(), System.currentTimeMillis(), type, amount, accountId);
    }

}