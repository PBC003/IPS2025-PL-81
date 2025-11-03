package ips.club.service;

import ips.club.dao.IncidentDao;
import ips.club.dao.IncidentTypeDao;
import ips.club.model.Incident;
import ips.club.model.IncidentType;
import ips.util.DatabaseTest;
import ips.util.ApplicationException;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class IncidentServiceTest extends DatabaseTest {

    private int anyValidIncidentType() {
        List<IncidentType> types = new IncidentTypeDao().findAll();
        assertFalse("Debe haber tipos de incidencia cargados por data.sql", types.isEmpty());
        return types.get(0).getCode();
    }

    @Test
    public void createTicketTest() {
        IncidentService svc = new IncidentService();
        int incCode = anyValidIncidentType();

        LocalDateTime before = LocalDateTime.now();
        Incident out = svc.createTicket(1, incCode, "Prueba 2", 1);
        LocalDateTime after = LocalDateTime.now();

        assertNotNull("El ID debe venir asignado tras insertar", out.getId());
        assertEquals(1, out.getUserId());
        assertEquals(incCode, out.getIncCode());
        assertEquals("Prueba 2", out.getDescription());

        assertNotNull(out.getCreatedAt());
        assertFalse(out.getCreatedAt().isBefore(before.minusSeconds(2)));
        assertFalse(out.getCreatedAt().isAfter(after.plusSeconds(2)));

        List<Incident> all = new IncidentDao().findAll();
        boolean found = false;
        for (Incident i : all) {
            if (i.getId().equals(out.getId())) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    @Test(expected = ApplicationException.class)
    public void createTicket_InvalidUserId() {
        new IncidentService().createTicket(0, anyValidIncidentType(), "desc", 1);
    }

    @Test(expected = ApplicationException.class)
    public void createTicket_InvalidIncCode() {
        new IncidentService().createTicket(1, -1, "desc", 1);
    }

    @Test(expected = ApplicationException.class)
    public void createTicket_EmptyDetails() {
        new IncidentService().createTicket(1, anyValidIncidentType(), "   ", 1);
    }
}
