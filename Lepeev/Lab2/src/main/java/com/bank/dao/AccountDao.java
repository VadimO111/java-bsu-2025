package com.bank.dao;

import com.bank.config.DatabaseConnection;
import com.bank.model.Account;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AccountDao {
    public void save(Account account) {
        try (var ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement("MERGE INTO accounts KEY(id) VALUES (?, ?, ?, ?)")) {
            ps.setString(1, account.getId().toString());
            ps.setString(2, account.getUserUuid().toString());
            ps.setDouble(3, account.getBalance());
            ps.setBoolean(4, account.isFrozen());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public Account findById(UUID id) {
        try (var ps = DatabaseConnection.getInstance().getConnection().prepareStatement("SELECT * FROM accounts WHERE id = ?")) {
            ps.setString(1, id.toString());
            var rs = ps.executeQuery();
            if (rs.next()) {
                return new Account(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("user_uuid")),
                        rs.getDouble("balance"),
                        rs.getBoolean("is_frozen")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public List<Account> findAll() {
        List<Account> list = new ArrayList<>();
        try (var rs = DatabaseConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM accounts")) {
            while (rs.next()) {
                list.add(new Account(
                        UUID.fromString(rs.getString("id")),
                        UUID.fromString(rs.getString("user_uuid")),
                        rs.getDouble("balance"),
                        rs.getBoolean("is_frozen")
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}