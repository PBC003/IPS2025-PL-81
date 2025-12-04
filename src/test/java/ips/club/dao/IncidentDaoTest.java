package ips.club.dao;

import ips.club.model.Incident;
import ips.club.model.IncidentStatus;
import ips.club.model.Location;
import ips.util.Database;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class IncidentDaoTest extends DatabaseTest {
    @Test
    public void insert_and_list_roundTrip_withDefaultOpenAndOptionalLocation() {
        Database db = new Database();
        db.createDatabase(false);
        db.loadDatabase();
        IncidentDao dao = new IncidentDao();

        Integer locationId = null;
        List<Location> locs = new LocationDao().findAll();
        if (locs != null && !locs.isEmpty()) {
            locationId = locs.get(0).getId();
        }

        Incident toInsert = new Incident(
                null,
                1,
                1,
                "Incidencia de prueba (OPEN por defecto)",
                LocalDateTime.now(),
                locationId);

        Incident inserted = dao.insert(toInsert);
        assertNotNull("El insert debe devolver la entidad con id", inserted);
        assertNotNull("El id debe ser autogenerado", inserted.getId());
        assertTrue("El id debe ser > 0", inserted.getId() > 0);

        List<Incident> all = dao.findAll();
        assertNotNull(all);
        assertFalse("La lista no debería estar vacía tras insertar", all.isEmpty());

        Incident found = null;
        for (Incident i : all) {
            if (i.getId().equals(inserted.getId())) {
                found = i;
                break;
            }
        }

        assertTrue("La incidencia insertada debe aparecer en findAll()", found != null);
        assertEquals("userId debe coincidir", 1, found.getUserId());
        assertEquals("incCode debe coincidir", 1, found.getIncCode());
        assertEquals("description debe coincidir", "Incidencia de prueba (OPEN por defecto)", found.getDescription());
        assertNotNull("createdAt no debe ser null", found.getCreatedAt());
        assertEquals("Status por defecto debe ser OPEN", IncidentStatus.OPEN, found.getStatus());
    }
}
