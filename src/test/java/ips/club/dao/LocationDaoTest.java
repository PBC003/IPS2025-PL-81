package ips.club.dao;

import ips.club.model.Location;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class LocationDaoTest extends DatabaseTest {

    @Test
    public void findAll_returnsSeededLocations() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<Location> locs = new LocationDao().findAll();
        assertNotNull(locs);
        assertFalse(locs.isEmpty());
        boolean hasGym = false;
        for (Location l : locs) {
            if ("Gym".equals(l.getName())) {
                hasGym = true;
                break;
            }
        }
        assertTrue(hasGym);
    }
}
