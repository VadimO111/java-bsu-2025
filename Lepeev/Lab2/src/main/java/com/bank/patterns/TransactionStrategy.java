package com.bank.patterns;
import com.bank.model.Account;

public interface TransactionStrategy {
    void execute(Account account, double amount);
}