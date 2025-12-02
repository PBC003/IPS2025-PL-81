package ips.club.dao;

import ips.club.model.Location;
import ips.util.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LocationDao {

    private static final String SQL_FIND_ALL_ACTIVE = "SELECT id, name, outdoor FROM location ORDER BY name";
    private static final String SQL_FIND_BY_ID = "SELECT id, name, outdoor FROM location WHERE id = ?";

    public List<Location> findAll() {
        List<Location> res = new ArrayList<>();
        Database db = new Database();
        try (Connection c = db.getConnection()){
            PreparedStatement ps = c.prepareStatement(SQL_FIND_ALL_ACTIVE);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                boolean outdoor = rs.getBoolean("outdoor");
                res.add(new Location(id, name, outdoor));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando localizaciones", e);
        }
        return res;
    }

    public Location findById(int id) {
        Database db = new Database();
        try (Connection c = db.getConnection()){
            PreparedStatement ps = c.prepareStatement(SQL_FIND_BY_ID);

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    boolean outdoor = rs.getBoolean("outdoor");
                    return new Location(id, name, outdoor);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error cargando localización", e);
        }
    }
}
