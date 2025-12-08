package com.bank.patterns;

import com.bank.model.Account;
import com.bank.model.User;

public class BankReportVisitor implements Visitor {
    private final StringBuilder report = new StringBuilder();

    public void visit(User user) {
        report.append("Клиент: ").append(user.nickname())
                .append(" [ID: ").append(user.uuid()).append("]\n");
    }

    public void visit(Account account) {
        report.append("   └── Счет: ").append(account.getId())
                .append(" | Баланс: ").append(String.format("%.2f", account.getBalance()))
                .append(account.isFrozen() ? " (Заморожен)" : "")
                .append("\n");
    }

    public String getReport() {
        return report.toString();
    }
}