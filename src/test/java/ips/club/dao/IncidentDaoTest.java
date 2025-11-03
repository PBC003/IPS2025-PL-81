package ips.club.dao;

import ips.club.model.Incident;
import ips.club.model.IncidentStatus;
import ips.util.DatabaseTest;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;

public class IncidentDaoTest extends DatabaseTest {

    @Test
    public void insert_and_findAll() {
        IncidentDao dao = new IncidentDao();

        Incident inc = new Incident(null, 2, 1, "Descripción de prueba", LocalDateTime.now(), IncidentStatus.OPEN, 1);
        Incident saved = dao.insert(inc);

        assertNotNull(saved.getId());
        assertEquals(2, saved.getUserId());
        assertEquals(1, saved.getIncCode());
        assertEquals("Descripción de prueba", saved.getDescription());
        assertNotNull(saved.getCreatedAt());

        List<Incident> all = dao.findAll();
        boolean found = false;
        for (Incident i : all) {
            if (i.getId().equals(saved.getId())) {
                found = true;
                break;
            }
        }

        assertTrue(found);
    }

    @Test
    public void findAll_Correct() {
        List<Incident> all = new IncidentDao().findAll();
        assertFalse(all.isEmpty());
        assertNotNull(all.get(0).getCreatedAt());
    }
}
