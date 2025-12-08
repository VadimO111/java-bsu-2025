package com.bank.dao;

import com.bank.config.DatabaseConnection;
import com.bank.model.Transaction;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionDao {
    public void save(Transaction tx) {
        try (var ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement("INSERT INTO transactions (uuid, type, amount, account_id, timestamp) VALUES (?, ?, ?, ?, ?)")) {
            ps.setString(1, tx.uuid().toString());
            ps.setString(2, tx.type());
            ps.setDouble(3, tx.amount());
            ps.setString(4, tx.accountId().toString());
            ps.setLong(5, tx.timestamp());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        try (var rs = DatabaseConnection.getInstance().getConnection().createStatement()
                .executeQuery("SELECT * FROM transactions ORDER BY timestamp DESC")) {
            while (rs.next()) {
                list.add(new Transaction(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getLong("timestamp"),
                        rs.getString("type"),
                        rs.getDouble("amount"),
                        UUID.fromString(rs.getString("account_id"))
                ));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}