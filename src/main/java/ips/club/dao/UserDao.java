package ips.club.dao;

import ips.club.model.User;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String SQL_FIND_ALL = "SELECT id, name, surname, email, iban FROM Users ORDER BY name";

    private static final String SQL_FIND_BY_ID = "SELECT id, name, last_name, email, iban FROM Users WHERE id = ?";

    public User findBasicById(int userId) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        rs.getString("iban")
                    );
                } else {
                    throw new ApplicationException("Usuario no encontrado con ID: " + userId);
                }
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al buscar usuario por ID");
        }
    }

    public List<User> findAll() {
        List<User> out = new ArrayList<>();
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("surname"), rs.getString("email"), rs.getString("iban")));
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al listar usuarios");
        }
    }
}
