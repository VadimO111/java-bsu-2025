package com.bank.patterns;

import com.bank.model.Account;
import com.bank.model.User;

public interface Visitor {
    void visit(User user);
    void visit(Account account);
    String getReport();
}