package ips.club.dao;

import ips.club.model.User;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDao {
    private static final String SQL_FIND_ALL = "SELECT id, name, surname, email, iban, monthly_fee_cents, role FROM Users ORDER BY name";

    private static final String SQL_FIND_BY_ID = "SELECT id, name, surname, email, iban, monthly_fee_cents, role FROM Users WHERE id = ?";

    private static final String SQL_FIND_ALL_WITHOUT_RECEIPT_FOR = "SELECT u.id, u.name, u.surname, u.email, u.iban, u.monthly_fee_cents, u.role " +
            "FROM Users u " +
            "LEFT JOIN Receipt r ON r.user_id = u.id AND r.charge_month = ? " +
            "WHERE r.id IS NULL " +
            "ORDER BY u.surname, u.name";

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
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("iban"),
                            rs.getInt("monthly_fee_cents"),
                            rs.getString("role"));
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
                out.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("surname"),
                        rs.getString("email"), rs.getString("iban"), rs.getInt("monthly_fee_cents"),
                        rs.getString("role")));
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al listar usuarios");
        }
    }

    public List<User> findAllWithoutReceiptFor(String yyyymm) {
        List<User> out = new ArrayList<>();
        Database db = new Database();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL_WITHOUT_RECEIPT_FOR)) {
            ps.setString(1, yyyymm);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("surname"),
                            rs.getString("email"),
                            rs.getString("iban"),
                            rs.getInt("monthly_fee_cents"),
                            rs.getString("role")));
                }
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al listar usuarios sin recibo para " + yyyymm);
        }
    }
}
