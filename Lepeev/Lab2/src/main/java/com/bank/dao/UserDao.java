package com.bank.dao;

import com.bank.config.DatabaseConnection;
import com.bank.model.User;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDao {
    public void save(User user) {
        try (var ps = DatabaseConnection.getInstance().getConnection()
                .prepareStatement("MERGE INTO users KEY(uuid) VALUES (?, ?)")) {
            ps.setString(1, user.uuid().toString());
            ps.setString(2, user.nickname());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (var rs = DatabaseConnection.getInstance().getConnection().createStatement().executeQuery("SELECT * FROM users")) {
            while (rs.next()) {
                list.add(new User(UUID.fromString(rs.getString("uuid")), rs.getString("nickname")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
}