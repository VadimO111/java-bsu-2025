package com.bank.patterns;

import com.bank.dao.AccountDao;
import com.bank.dao.TransactionDao;
import com.bank.model.Account;
import com.bank.model.Transaction;
import java.util.UUID;

public record TransactionCommand(String type, double amount, String accountId, AccountDao accountDao,
                                 TransactionDao transactionDao, EventManager eventManager) implements Command {

    public void execute() {
        try {
            UUID uuid = UUID.fromString(accountId);
            Account account = accountDao.findById(uuid);
            if (account == null) throw new IllegalArgumentException("Счет не найден");

            TransactionStrategy strategy = TransactionFactory.getStrategy(type);
            if (strategy != null) {
                synchronized (account) {
                    strategy.execute(account, amount);
                    Transaction tx = new Transaction(type, amount, uuid);
                    transactionDao.save(tx);
                    accountDao.save(account);
                    eventManager.notify("Транзакция " + type + " на сумму " + amount + " выполнена успешно.");
                }
            }
        } catch (Exception e) {
            eventManager.notify("Ошибка: " + e.getMessage());
        }
    }
}