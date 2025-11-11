package ips.club.dao;

import ips.club.model.Reservation;
import ips.util.ApplicationException;
import ips.util.Database;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationDao {

    private static final String SQL_INSERT =
        "INSERT INTO Reservation(user_id, location_id, start_time, end_time, minutes, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_OVERLAPS_AT_LOCATION =
        "SELECT * FROM Reservation " +
        "WHERE location_id = ? " +
        "  AND start_time < ? " +
        "  AND end_time   > ?";

    private static final String SQL_OVERLAPS_FOR_USER =
        "SELECT * FROM Reservation " +
        "WHERE user_id = ? " +
        "  AND start_time < ? " +
        "  AND end_time   > ?";

    private static final String SQL_SUM_MINUTES_USER_LOC_DAY =
        "SELECT COALESCE(SUM(minutes), 0) AS total " +
        "FROM Reservation " +
        "WHERE user_id = ? AND location_id = ? " +
        "  AND start_time < ? " +
        "  AND end_time   > ?";

    private static final String SQL_LIST_ALL =
        "SELECT * FROM Reservation ORDER BY start_time DESC";

    private static final String SQL_LIST_BY_LOCATION =
        "SELECT * FROM Reservation WHERE location_id = ? ORDER BY start_time DESC";


    public int insert(Reservation r) {
        Database db = new Database();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, r.getUserId());
            ps.setInt(2, r.getLocationId());
            ps.setString(3, r.startAsText());
            ps.setString(4, r.endAsText());
            ps.setInt(5, r.getMinutes());
            ps.setString(6, r.createdAtAsText());

            int n = ps.executeUpdate();
            if (n != 1) throw new SQLException("No se insertó la reserva");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    r.setId(id);
                    return id;
                }
            }
            throw new SQLException("Insert sin ID generado");
        } catch (SQLException e) {
            throw new ApplicationException("Error insertando reserva");
        }
    }

    public List<Reservation> findOverlapsAtLocation(int locationId, LocalDateTime newStart, LocalDateTime newEnd) {
        List<Reservation> out = new ArrayList<>();
        Database db = new Database();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_OVERLAPS_AT_LOCATION)) {

            ps.setInt(1, locationId);
            ps.setString(2, newEnd.format(Reservation.FMT));
            ps.setString(3, newStart.format(Reservation.FMT));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error buscando solapes en instalación");
        }
        return out;
    }

    public List<Reservation> findOverlapsForUser(int userId, LocalDateTime newStart, LocalDateTime newEnd) {
        List<Reservation> out = new ArrayList<>();
        Database db = new Database();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_OVERLAPS_FOR_USER)) {

            ps.setInt(1, userId);
            ps.setString(2, newEnd.format(Reservation.FMT));
            ps.setString(3, newStart.format(Reservation.FMT));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error buscando solapes del usuario");
        }
        return out;
    }

    public int sumMinutesUserAtLocationOn(LocalDate date, int userId, int locationId) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay   = date.atTime(LocalTime.of(23, 59, 59));

        Database db = new Database();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SUM_MINUTES_USER_LOC_DAY)) {

            ps.setInt(1, userId);
            ps.setInt(2, locationId);
            ps.setString(3, endOfDay.format(Reservation.FMT));
            ps.setString(4, startOfDay.format(Reservation.FMT));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("total");
                return 0;
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error sumando minutos del día");
        }
    }

    public List<Reservation> listAll() {
        List<Reservation> out = new ArrayList<>();
        Database db = new Database();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LIST_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(map(rs));
        } catch (SQLException e) {
            throw new ApplicationException("Error listando reservas");
        }
        return out;
    }

    public List<Reservation> listByLocation(int locationId) {
        List<Reservation> out = new ArrayList<>();
        Database db = new Database();
        try (Connection con = db.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_LIST_BY_LOCATION)) {

            ps.setInt(1, locationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(map(rs));
            }
        } catch (SQLException e) {
            throw new ApplicationException("Error listando reservas por instalación");
        }
        return out;
    }

    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setId(rs.getInt("id"));
        r.setUserId(rs.getInt("user_id"));
        r.setLocationId(rs.getInt("location_id"));
        r.setStart(LocalDateTime.parse(rs.getString("start_time"), Reservation.FMT));
        r.setEnd(LocalDateTime.parse(rs.getString("end_time"), Reservation.FMT));
        r.setMinutes(rs.getInt("minutes"));
        r.setCreatedAt(LocalDateTime.parse(rs.getString("created_at"), Reservation.FMT));
        return r;
    }
}
