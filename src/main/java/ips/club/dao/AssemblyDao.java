package ips.club.dao;

import ips.club.model.Assembly;
import ips.club.model.AssemblyStatus;
import ips.club.model.AssemblyType;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AssemblyDao {

    private static final String SQL_INSERT =
        "INSERT INTO Assembly (title, description, scheduled_at, created_at, status, type, minutes_text) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_MINUTES_WAITING =
        "UPDATE Assembly SET minutes_text = ?, status = 'WAITING' WHERE id = ?";

    private static final String SQL_MARK_FINISHED =
        "UPDATE Assembly SET status = 'FINISHED' WHERE id = ?";

    private static final String SQL_FIND_BY_ID =
        "SELECT id, title, description, scheduled_at, created_at, status, type, minutes_text " +
        "FROM Assembly WHERE id = ?";

    private static final String SQL_FIND_ALL =
        "SELECT id, title, description, scheduled_at, created_at, status, type, minutes_text " +
        "FROM Assembly ORDER BY scheduled_at DESC";

    private static final String SQL_FIND_BY_STATUS =
        "SELECT id, title, description, scheduled_at, created_at, status, type, minutes_text " +
        "FROM Assembly WHERE status = ? ORDER BY scheduled_at DESC";

    private static final String SQL_EXISTS_ORDINARY_YEAR =
        "SELECT 1 FROM Assembly WHERE type='ORDINARY' AND substr(scheduled_at,1,4)=? LIMIT 1";

    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private static String toDb(LocalDateTime dt) {
        return dt == null ? null : dt.withNano(0).format(ISO);
    }

    private static LocalDateTime fromDb(String s) {
        return (s == null || s.isEmpty()) ? null : LocalDateTime.parse(s, ISO);
    }

    private Assembly map(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String description = rs.getString("description");
        LocalDateTime scheduledAt = fromDb(rs.getString("scheduled_at"));
        LocalDateTime createdAt = fromDb(rs.getString("created_at"));
        AssemblyStatus status = AssemblyStatus.fromDb(rs.getString("status"));
        AssemblyType type = AssemblyType.fromDb(rs.getString("type"));
        String minutes = rs.getString("minutes_text");
        return new Assembly(id, title, description, scheduledAt, createdAt, status, type, minutes);
    }

    public Assembly insert(Assembly a) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, a.getTitle());
            ps.setString(2, a.getDescription());
            ps.setString(3, toDb(a.getScheduledAt()));
            ps.setString(4, toDb(a.getCreatedAt()));
            ps.setString(5, a.getStatus().toDb());
            ps.setString(6, a.getType().toDb());
            ps.setString(7, a.getMinutesText());

            int rows = ps.executeUpdate();
            if (rows != 1) throw new ApplicationException("No se insertó la asamblea");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) a.setId(keys.getInt(1));
            }
            return a;

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al insertar asamblea");
        }
    }

    public boolean updateMinutesAndMarkWaiting(int id, String minutesText) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_UPDATE_MINUTES_WAITING)) {

            ps.setString(1, minutesText);
            ps.setInt(2, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al guardar acta y marcar WAITING");
        }
    }

    public boolean markFinished(int id) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_MARK_FINISHED)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al marcar FINISHED");
        }
    }

    public Assembly findById(int id) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return map(rs);
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al buscar asamblea por id");
        }
    }

    public List<Assembly> findAll() {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Assembly> out = new ArrayList<>();
            while (rs.next()) out.add(map(rs));
            return out;

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al listar asambleas");
        }
    }

    public List<Assembly> findByStatus(AssemblyStatus status) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_FIND_BY_STATUS)) {

            ps.setString(1, status.toDb());
            try (ResultSet rs = ps.executeQuery()) {
                List<Assembly> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al filtrar por estado");
        }
    }

    public List<Assembly> findFiltered(AssemblyStatus status, LocalDateTime from, LocalDateTime to) {
        StringBuilder sb = new StringBuilder(
            "SELECT id, title, description, scheduled_at, created_at, status, type, minutes_text FROM Assembly WHERE 1=1"
        );
        ArrayList<Object> params = new ArrayList<>();

        if (status != null) {
            sb.append(" AND status = ?");
            params.add(status.toDb());
        }
        if (from != null) {
            sb.append(" AND scheduled_at >= ?");
            params.add(toDb(from));
        }
        if (to != null) {
            sb.append(" AND scheduled_at <= ?");
            params.add(toDb(to));
        }
        sb.append(" ORDER BY scheduled_at DESC");

        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(sb.toString())) {

            int idx = 1;
            if (status != null) ps.setString(idx++, params.get(0).toString());
            int p = (status != null) ? 1 : 0;
            if (from != null) ps.setString(idx++, params.get(p++).toString());
            if (to != null) ps.setString(idx, params.get(p).toString());

            try (ResultSet rs = ps.executeQuery()) {
                List<Assembly> out = new ArrayList<>();
                while (rs.next()) out.add(map(rs));
                return out;
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL en filtro combinado");
        }
    }

    public boolean existsOrdinaryInYear(int year) {
        Database db = new Database();
        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_EXISTS_ORDINARY_YEAR)) {

            ps.setString(1, String.valueOf(year));
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new ApplicationException("Error SQL al validar ordinaria por año");
        }
    }
}
