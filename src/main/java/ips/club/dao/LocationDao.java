package ips.club.dao;

import ips.club.model.Location;
import ips.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDao {

    private static final String SQL_FIND_ALL_ACTIVE = "SELECT id, name FROM location ORDER BY name";

    public List<Location> findAll() {
        List<Location> res = new ArrayList<>();
        Database db = new Database();
        try (Connection c = db.getConnection();
                PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL_ACTIVE);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                res.add(new Location(rs.getInt("id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando localizaciones", e);
        }
        return res;
    }
}
