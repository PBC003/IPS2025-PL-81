package ips.club.dao;

import ips.club.model.Incident;
import ips.club.model.IncidentStatus;
import ips.club.model.Location;
import ips.util.DatabaseTest;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class IncidentDaoTest extends DatabaseTest {
    @Test
    public void insert_and_list_roundTrip_withDefaultOpenAndOptionalLocation() {
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
                locationId
        );

        Incident inserted = dao.insert(toInsert);
        assertNotNull("El insert debe devolver la entidad con id", inserted);
        assertNotNull("El id debe ser autogenerado", inserted.getId());
        assertTrue("El id debe ser > 0", inserted.getId() > 0);

        List<Incident> all = dao.findAll();
        assertNotNull(all);
        assertFalse("La lista no debería estar vacía tras insertar", all.isEmpty());

        Optional<Incident> foundOpt = all.stream()
                .filter(i -> i.getId().equals(inserted.getId()))
                .findFirst();

        assertTrue("La incidencia insertada debe aparecer en findAll()", foundOpt.isPresent());
        Incident found = foundOpt.get();

        assertEquals("userId debe coincidir", 1, found.getUserId());
        assertEquals("incCode debe coincidir", 1, found.getIncCode());
        assertEquals("description debe coincidir", "Incidencia de prueba (OPEN por defecto)", found.getDescription());
        assertNotNull("createdAt no debe ser null", found.getCreatedAt());
        assertEquals("Status por defecto debe ser OPEN", IncidentStatus.OPEN, found.getStatus());
        assertEquals("locationId debe coincidir", locationId, found.getLocationId());
    }
}
