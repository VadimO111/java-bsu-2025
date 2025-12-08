package com.bank.patterns;
import com.bank.model.Account;

public class UnfreezeStrategy implements TransactionStrategy {
    public void execute(Account account, double amount) {
        account.setFrozen(false);
    }
}