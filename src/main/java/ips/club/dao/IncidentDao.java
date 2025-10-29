package ips.club.dao;

import ips.club.model.Incident;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;

public class IncidentDao {

    private static final String SQL_INSERT = "INSERT INTO incident (user_id, inc_code, description, created_at) VALUES (?, ?, ?, ?)";
    public static final String DRIVER = "org.sqlite.JDBC";
    public static final String URL = "jdbc:sqlite:DemoDB.db";

    Database db = new Database();

    public Incident insert(Incident i) {
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, i.getUserId());
            ps.setInt(2, i.getIncCode());
            ps.setString(3, i.getDescription());
            ps.setTimestamp(4, Timestamp.valueOf(i.getCreatedAt()));

            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new ApplicationException("Error al insertar incidencia: no se modificó ninguna fila.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                int id = rs.next() ? rs.getInt(1) : null;
                return new Incident(id, i.getUserId(), i.getIncCode(), i.getDescription(), i.getCreatedAt());
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al insertar incidencia");
        }
    }
}
