package com.bank.patterns;
import com.bank.model.Account;

public class WithdrawStrategy implements TransactionStrategy {
    public void execute(Account account, double amount) {
        if (account.isFrozen()) throw new IllegalStateException("Счет заморожен");
        if (account.getBalance() < amount) throw new IllegalArgumentException("Недостаточно средств");
        account.setBalance(account.getBalance() - amount);
    }
}