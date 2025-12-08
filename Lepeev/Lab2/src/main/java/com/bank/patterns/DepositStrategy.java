package com.bank.patterns;
import com.bank.model.Account;

public class DepositStrategy implements TransactionStrategy {
    public void execute(Account account, double amount) {
        if (account.isFrozen()) throw new IllegalStateException("Счет заморожен");
        account.setBalance(account.getBalance() + amount);
    }
}