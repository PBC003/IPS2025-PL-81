package ips.club.dao;

import ips.club.model.User;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String SQL_FIND_ALL = "SELECT id, name, email FROM Users ORDER BY name";

    public List<User> findAll() {
        List<User> out = new ArrayList<>();
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("email")));
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al listar usuarios");
        }
    }
}
