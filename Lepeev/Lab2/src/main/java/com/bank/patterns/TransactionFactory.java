package com.bank.patterns;

public class TransactionFactory {
    public static TransactionStrategy getStrategy(String type) {
        return switch (type) {
            case "DEPOSIT" -> new DepositStrategy();
            case "WITHDRAW" -> new WithdrawStrategy();
            case "FREEZE" -> new FreezeStrategy();
            case "UNFREEZE" -> new UnfreezeStrategy();
            default -> null;
        };
    }
}