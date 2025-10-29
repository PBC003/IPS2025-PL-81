package ips.club.service.demos;

import ips.club.service.IncidentService;

import java.util.List;

import ips.club.dao.IncidentDao;
import ips.club.model.Incident;
import ips.util.Database;

public class CreateTicketDemo {
    public static void main(String[] args) {
        Database db = new Database();
        db.createDatabase(true);

        IncidentService svc = new IncidentService();

        int userId = 1;
        int incCode = 1;
        String details = "Prueba";
        Incident created = svc.createTicket(userId, incCode, details);

        System.out.println("Incidencia creada: id=" + created.getId()
                + ", userId=" + created.getUserId()
                + ", incCode=" + created.getIncCode()
                + ", desc=\"" + created.getDescription() + "\""
                + ", createdAt=" + created.getCreatedAt());

        userId = 2;
        incCode = 2;
        details = "Prueba 2";
        svc.createTicket(userId, incCode, details);

        IncidentDao dao = new IncidentDao();
        List<Incident> all = dao.findAll();

        System.out.println("=== Incidencias en BD ===");
        for (Incident i : all) {
            System.out.println("id=" + i.getId()
                    + " | userId=" + i.getUserId()
                    + " | incCode=" + i.getIncCode()
                    + " | desc=\"" + i.getDescription() + "\""
                    + " | createdAt=" + i.getCreatedAt());
        }
    }
}
