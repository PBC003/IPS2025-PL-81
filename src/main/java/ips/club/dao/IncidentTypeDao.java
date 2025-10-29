package ips.club.dao;

import ips.club.model.IncidentType;
import ips.club.model.IncidentType.FieldType;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentTypeDao {
    private static final String SQL_FIND_ALL = "SELECT code, name, type FROM Incident_type ORDER BY name";

    public List<IncidentType> findAll() {
        List<IncidentType> out = new ArrayList<>();
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                out.add(new IncidentType(
                        rs.getInt("code"),
                        rs.getString("name"),
                        FieldType.fromDb(rs.getString("type"))
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al listar tipos de incidencia");
        }
    }
}
