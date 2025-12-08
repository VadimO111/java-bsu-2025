package com.bank.patterns;
import com.bank.model.Account;

public class FreezeStrategy implements TransactionStrategy {
    public void execute(Account account, double amount) {
        account.setFrozen(true);
    }
}