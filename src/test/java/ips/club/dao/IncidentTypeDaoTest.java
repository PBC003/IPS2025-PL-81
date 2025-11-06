package ips.club.dao;

import ips.club.model.IncidentType;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class IncidentTypeDaoTest extends DatabaseTest {

    @Test
    public void findAll_returnsSeededTypes() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        List<IncidentType> types = new IncidentTypeDao().findAll();
        assertNotNull(types);
        assertFalse(types.isEmpty());
        boolean hasInstalaciones = false;
        for (IncidentType t : types) {
            if ("Instalaciones".equals(t.getName())) {
                hasInstalaciones = true;
                break;
            }
        }
        assertTrue(hasInstalaciones);
    }
}
