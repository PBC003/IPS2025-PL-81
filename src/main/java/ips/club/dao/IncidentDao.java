package ips.club.dao;

import ips.club.model.Incident;
import ips.club.model.IncidentStatus;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class IncidentDao {

	private static final String SQL_INSERT =
		    "INSERT INTO Incident (user_id, inc_code, description, created_at, location_id) VALUES (?, ?, ?, ?, ?)";

	private static final String SQL_FIND_ALL =
		    "SELECT id, user_id, inc_code, description, created_at, status, location_id FROM Incident ORDER BY id";

	public Incident insert(Incident i) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, i.getUserId());
            ps.setInt(2, i.getIncCode());
            ps.setString(3, i.getDescription());
            String iso = i.getCreatedAt()
                    .withNano(0)
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            ps.setString(4, iso);
            if (i.getLocationId() == null) ps.setNull(5, Types.INTEGER);
            else ps.setInt(5, i.getLocationId());

            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new ApplicationException("Error al insertar incidencia: no se modificó ninguna fila.");
            }

            try (ResultSet rs = ps.getGeneratedKeys()) {
                int id = rs.next() ? rs.getInt(1) : null;
                return new Incident(id, i.getUserId(), i.getIncCode(), i.getDescription(), i.getCreatedAt(), i.getStatus(), i.getLocationId());
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al insertar incidencia");
        }
    }

    public List<Incident> findAll() {
        Database db = new Database();
        try (Connection conn = db.getConnection();
                PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
                ResultSet rs = ps.executeQuery()) {

            List<Incident> out = new ArrayList<>();
            while (rs.next()) {
                out.add(map(rs));
            }
            return out;

        } catch (SQLException e) {
            e.printStackTrace();
            throw new ApplicationException("Error SQL al listar incidencias");
        }
    }

    private Incident map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int incCode = rs.getInt("inc_code");
        String description = rs.getString("description");
        LocalDateTime createdAt = LocalDateTime.parse(
        	    rs.getString("created_at"),
        	    DateTimeFormatter.ISO_LOCAL_DATE_TIME
        	);
        IncidentStatus status =  IncidentStatus.fromDb(rs.getString("status"));
        Integer locationId = (Integer) rs.getObject("location_id");

        return new Incident(id, userId, incCode, description, createdAt, status, locationId);
    }
}
