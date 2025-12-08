package com.bank.service;

import com.bank.dao.AccountDao;
import com.bank.dao.TransactionDao;
import com.bank.dao.UserDao;
import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.patterns.*;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BankService {
    private final UserDao userDao = new UserDao();
    private final AccountDao accountDao = new AccountDao();
    private final TransactionDao transactionDao = new TransactionDao();
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    private final EventManager eventManager = new EventManager();

    public BankService() {
        if (userDao.findAll().isEmpty()) {
            initDemoData();
        }
    }

    public void subscribe(Consumer<String> listener) {
        eventManager.subscribe(listener);
    }

    private void initDemoData() {
        createUser("Vadim1111");
        createUser("van rebuild");
        createUser("Tester_User");

        List<User> users = userDao.findAll();
        UUID vadimId = users.stream().filter(u -> u.nickname().equals("Vadim1111")).findFirst().get().uuid();
        UUID vanId = users.stream().filter(u -> u.nickname().equals("van rebuild")).findFirst().get().uuid();
        UUID testerId = users.stream().filter(u -> u.nickname().equals("Tester_User")).findFirst().get().uuid();

        createAccount(vadimId.toString());
        createAccount(vadimId.toString());
        createAccount(vanId.toString());
        createAccount(testerId.toString());

        try { Thread.sleep(500); } catch (Exception e) {}
        List<Account> accounts = accountDao.findAll();

        processTransactionAsync("DEPOSIT", 50000, accounts.get(0).getId().toString());
        processTransactionAsync("DEPOSIT", 1000000, accounts.get(2).getId().toString());
        processTransactionAsync("WITHDRAW", 100, accounts.get(0).getId().toString());
        processTransactionAsync("DEPOSIT", 2500, accounts.get(1).getId().toString());
    }

    public void createUser(String nickname) {
        User user = new User(nickname);
        userDao.save(user);
        eventManager.notify("Пользователь создан: " + nickname);
    }

    public void createAccount(String userUuidStr) {
        try {
            Account account = new Account(UUID.fromString(userUuidStr));
            accountDao.save(account);
            eventManager.notify("Счет открыт: " + account.getId());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void processTransactionAsync(String type, double amount, String accountIdStr) {
        Command command = new TransactionCommand(type, amount, accountIdStr, accountDao, transactionDao, eventManager);
        executor.submit(command::execute);
    }

    public String generateReport() {
        BankReportVisitor visitor = new BankReportVisitor();
        List<User> users = userDao.findAll();
        List<Account> accounts = accountDao.findAll();

        for (User user : users) {
            user.accept(visitor);
            accounts.stream()
                    .filter(a -> a.getUserUuid().equals(user.uuid()))
                    .forEach(a -> a.accept(visitor));
        }
        return visitor.getReport();
    }

    public List<User> getAllUsers() { return userDao.findAll(); }
    public List<Account> getAllAccounts() { return accountDao.findAll(); }
    public List<Transaction> getAllTransactions() { return transactionDao.findAll(); }
}